import React, {useEffect, useState} from "react";
import axios from "axios";
import {toast, ToastContainer} from "react-toastify";
import {Button, Modal} from "react-bootstrap";
import * as XLSX from "xlsx";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {faLock, faUnlock} from '@fortawesome/free-solid-svg-icons';
import UserManagementAdd from "./UserManagementAdd";
import {Link} from "react-router-dom";
import RootPathApi from "../../../route/RootPathApi";

export default function UserManagement(){
    const [users, setUsers] = useState([]);
    const [showAdd, setShowAdd] = useState(false);

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [isAllSelected, setIsAllSelected] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const baseUrl = RootPathApi();

    useEffect(() => {
        axios.get(`${baseUrl}/api/v1/management/users`)
            .then(res=>{
                setUsers(res.data);
                console.log(res.data);
            }).catch(error => {
            console.log("error:" + error)
        });
    },[refresh])

    const handleUserSelection = (userId) => {
        if (selectedUsers.includes(userId)) {
            setSelectedUsers(selectedUsers.filter((id) => id !== userId));
        } else {
            setSelectedUsers([...selectedUsers, userId]);
        }
        console.log(selectedUsers)
    };

    const handleSelectAll = () => {
        if (isAllSelected) {
            setSelectedUsers([]);
        } else {
            setSelectedUsers(users.map(u => u.id));
        }
        setIsAllSelected(!isAllSelected);
    };

    const handleClearSelection = () => {
        if (selectedUsers.length < 2) {
            setSelectedUsers([]);
        } else {
            setSelectedUsers([...selectedUsers]);
        }
        console.log(selectedUsers)
    };

    const deleteMultipleUsers = async () => {
        try {
            // Get remaining users after the deletion
            const remainingUsers = users.filter(user => !selectedUsers.includes(user.id));

            // Check if there is at least one admin remaining
            const remainingAdmins = remainingUsers.filter(user => user.roles.includes('ADMIN'));
            if (remainingAdmins.length === 0) {
                toast.error('Không thể xóa, phải có ít nhất một người dùng với quyền ADMIN!');
                return;
            }
            const response = await axios.delete(`${baseUrl}/api/v1/management/users/deletes`, {
                data: {
                    userIds: selectedUsers,
                }
            });
            setRefresh(prev => !prev);

            // Gọi lại hook bằng cách thay đổi refreshTable
            setShowDeleteModal(false);
            setCurrentPage(1)
            setSelectedUsers([])
            setIsAllSelected(false)
            notify()
            console.log(response.status);
        } catch (error) {
            console.error('Error deleting users:', error);
        }
    };

    const notify = () => {
        toast.success('Xoá thành công', {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
        });
    }

    const [dropdownOpen, setDropdownOpen] = useState(false);
    const toggleDropdown = () => {
        setDropdownOpen(prevState => !dropdownOpen);
    };
    const [searchTerm, setSearchTerm] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [itemPerPage, setItemPerPage] = useState(5);

    // Tìm kiếm đơn hàng
    const filteredItems = users.filter((order) => {
        const orderValues = Object.values(order).join(' ').toLowerCase();
        return orderValues.includes(searchTerm.toLowerCase());
    });

    // Lấy danh sách đơn hàng cho trang hiện tại
    const indexOfLast = currentPage * itemPerPage;
    const indexOfFirst = indexOfLast - itemPerPage;
    const currentItems = filteredItems.slice(indexOfFirst, indexOfLast);

    // Chuyển trang
    const paginate = (pageNumber) => setCurrentPage(pageNumber);

    // Render số trang
    const pageNumbers = [];
    for (let i = 1; i <= Math.ceil(filteredItems.length / itemPerPage); i++) {
        pageNumbers.push(i);
    }

    const handleSearch = (event) => {
        setSearchTerm(event.target.value);
        setCurrentPage(1); // Đặt lại trang hiện tại về 1 khi tìm kiếm
    };


    const onExportExcel = () => {
        const worksheet = XLSX.utils.json_to_sheet(users);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Users');
        XLSX.writeFile(workbook, 'users.xlsx');
    };



    const renderIcon = (isEnabled) => {
        if (isEnabled) {
            return <FontAwesomeIcon icon={faUnlock} />;
        } else {
            return <FontAwesomeIcon icon={faLock} />;
        }
    };

    return(
        <div>
            <ToastContainer
                position="top-right"
                autoClose={5000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="colored"
            />
            <button className={"btn btn-success mb-2 p-2"} onClick={() => setShowAdd(true)}>+ Thêm người dùng
            </button>

            <div className={"d-flex align-items-center mb-3"}>
                <p className={"m-0"}>Hiển thị:</p>
                <select className="form-select col-1" onChange={(e) => setItemPerPage(e.target.value)}>
                    <option selected value="5">5</option>
                    <option value="10">10</option>
                    <option value="25">25</option>
                </select>
                <div className="btn-group dropend no-print ml-2 mr-2" style={{ position: 'relative' }}>
                    <button type="button" className="btn btn-primary dropdown-toggle" onClick={toggleDropdown}>
                        Công cụ
                    </button>
                    <ul className={`dropdown-menu tool ${dropdownOpen ? "show" : ""}`}>
                        <li className="dropdown-item "><a>In</a></li>
                        <li className="dropdown-item" onClick={onExportExcel}><a>Xuất ra exel</a></li>

                    </ul>
                </div>

                <button className={"btn btn-danger"} hidden={selectedUsers.length > 0 ? false : true}
                        onClick={() => setShowDeleteModal(true)}>Xóa nhiều
                </button>
                <input className="form-control ms-auto me-2 col-4" type="search" placeholder="Search"
                       aria-label="Search"
                       onChange={handleSearch}/>
            </div>

            <table className={"table text-left"}>
                <thead>
                <tr>
                    <th><input type={"checkbox"} onClick={() => handleSelectAll()}/></th>
                    <th>ID</th>
                    <th>Tên người dùng</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Quyền</th>
                    <th>Hiển thị</th>
                    <th></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {currentItems && currentItems.map(user => (
                    <tr key={user.id}>
                        <th><input className={"mt-4"} type={"checkbox"} checked={selectedUsers.includes(user.id)}
                                   onChange={() => handleUserSelection(user.id)}/></th>
                        <td>{user.id}</td>
                        <td>{user.fullname}</td>
                        <td>{user.email}</td>
                        <td>{user.phoneNumber}</td>
                        <td>{user.roles}</td>
                        <td className="text-center">
                            {renderIcon(user.enabled)}
                        </td>
                        <td className="text-center">
                            <Link to={`/admin/user-management/update/${user.id}`}>
                                <Button>Sửa</Button>
                            </Link>


                        </td>
                        <td className="text-center">
                            <Button
                                variant="danger"
                                onClick={() => {
                                    setShowDeleteModal(true)
                                    handleUserSelection(user.id)
                                }}
                                className="btn-delete"
                            >
                                Xóa
                            </Button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
            <nav className={"d-flex justify-content-end"}>
                <ul className="pagination">
                    {pageNumbers.map((number) => (
                        <li key={number} className={`page-item ${number === currentPage ? "active" : ""}`}>
                            <a
                                onClick={() => paginate(number)}
                                href="#"
                                className="page-link"
                            >
                                {number}
                            </a>
                        </li>
                    ))}
                </ul>
            </nav>
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Xóa bài viết</Modal.Title>
                </Modal.Header>
                <Modal.Body>Bạn có chắc chắn muốn xóa tài khoản này?</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => {
                        handleClearSelection()
                        setShowDeleteModal(false)

                    }}>
                        Hủy
                    </Button>
                    <Button variant="primary" onClick={deleteMultipleUsers}>
                        Xóa
                    </Button>
                </Modal.Footer>
            </Modal>
            <UserManagementAdd show={showAdd} onHide={() => {
                setShowAdd(false)
                setRefresh(pre => !pre)
            }}/>


        </div>
    )
}