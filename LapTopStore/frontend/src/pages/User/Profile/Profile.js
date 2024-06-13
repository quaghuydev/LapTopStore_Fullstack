import React, {useState} from 'react';
import {
    MDBBreadcrumb,
    MDBBreadcrumbItem,
    MDBBtn,
    MDBCard,
    MDBCardBody,
    MDBCardImage,
    MDBCardText,
    MDBCol,
    MDBContainer,
    MDBInput,
    MDBRow,
    MDBValidation,
    MDBValidationItem
} from 'mdb-react-ui-kit';
import axios from "axios";
import { ToastContainer, toast } from 'react-toastify';
import RootPathApi from "../../../route/RootPathApi";

function Profile() {
    const [password, setPassword] = useState('')
    const [curentPassword, setCurentPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [notify, setNotify] = useState('')
    const [successMessage, setSuccessMessage] = useState('');
    const baseUrl = RootPathApi()
    const validateForm = () => {
        if (curentPassword === '') {
            setNotify("Vui lòng nhập mật khẩu hiện tại")
        }
        if (password === '') {
            setNotify("Vui lòng nhập mật khẩu")
        }
        if (confirmPassword === '') {
            setNotify("Vui lòng xác nhận mật khẩu")
        }
        if (password === '' && confirmPassword === '') {
            setNotify("Vui lòng nhập đầy đủ 2 trường")

        }
        if (password.localeCompare(confirmPassword)) {
            setNotify("Mật khẩu không trùng khớp")
        } else {
            setNotify('')
        }
    }
    const handleLogin = async (e) => {
        validateForm();


        if (notify.localeCompare("")) {
            return
        }
        const userJson = sessionStorage.getItem("user");

        const user = JSON.parse(userJson);
        // console.log(password,user.email,curentPassword)

        try {
            const response = await axios.post(`${baseUrl}/api/v1/auth/change-password-profile`, {
                currentPass: curentPassword,
                newPasss: password,
                email:user.email
            }, {
                headers: {
                    'Content-Type': 'application/json',

                }
            });
            console.log(response.data)
            if (response.data) { // Kiểm tra nếu response.data là true
                setSuccessMessage("Đổi mật khẩu thành công!");
                 toast.success("Đổi mật khẩu thành công!");
                setCurentPassword("");
                setPassword("");
                setConfirmPassword("");
                setNotify("");


            } else {
                setNotify("Đổi mật khẩu thất bại!");// Hiển thị thông báo lỗi
                toast.error("Đổi mật khẩu thất bại!");
                setCurentPassword("");
                setPassword("");
                setConfirmPassword("");
                setSuccessMessage(" ");

            }




        } catch (e) {
            console.log(e)
        }


    }


    return (
        <section style={{backgroundColor: '#eee'}}>
            <MDBContainer className="py-2">
                <MDBRow>
                    <MDBCol>
                        <MDBBreadcrumb className="bg-light rounded-3 p-3 mb-4">

                            <MDBBreadcrumbItem>
                                <a href="#">Trang chủ</a>
                            </MDBBreadcrumbItem>
                            <MDBBreadcrumbItem active style={{color: "#e62e04"}}>Thông tin tài khoản</MDBBreadcrumbItem>
                        </MDBBreadcrumb>
                    </MDBCol>
                </MDBRow>

                <MDBRow>
                    <MDBCol lg="4" className={"pr-0"}>
                        <MDBCard className="mb-4 pb-3">
                            <MDBCardBody className="text-center">
                                <MDBCardImage
                                    src="https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-chat/ava3.webp"
                                    alt="avatar"
                                    className="rounded-circle"
                                    style={{width: '150px'}}
                                    fluid/>
                                <div className="d-flex justify-content-center mb-2 mt-5">
                                    <MDBBtn outline className="ms-1">Sửa thông tin</MDBBtn>
                                </div>
                            </MDBCardBody>
                        </MDBCard>

                    </MDBCol>
                    <MDBCol lg="8">
                        <MDBCard className="mb-4">
                            <MDBCardBody>
                                <MDBRow>
                                    <MDBCol sm="3">
                                        <MDBCardText>Họ và tên:</MDBCardText>
                                    </MDBCol>
                                    <MDBCol sm="9">
                                        <MDBCardText className="text-muted"> {JSON.parse(sessionStorage.getItem('user'))?.fullname ?? "Chưa có tên"}</MDBCardText>
                                    </MDBCol>
                                </MDBRow>
                                <hr/>
                                <MDBRow>
                                    <MDBCol sm="3">
                                        <MDBCardText>Email:</MDBCardText>
                                    </MDBCol>
                                    <MDBCol sm="9">
                                        <MDBCardText className="text-muted"> {JSON.parse(sessionStorage.getItem('user'))?.email ?? "Chưa có tên"}</MDBCardText>
                                    </MDBCol>
                                </MDBRow>
                                <hr/>
                                <MDBRow>
                                    <MDBCol sm="3">
                                        <MDBCardText>SĐT:</MDBCardText>
                                    </MDBCol>
                                    <MDBCol sm="9">
                                        <MDBCardText className="text-muted"> {JSON.parse(sessionStorage.getItem('user'))?.phoneNumber ?? "Chưa có tên"}</MDBCardText>
                                    </MDBCol>
                                </MDBRow>
                                <hr/>

                            </MDBCardBody>
                        </MDBCard>

                        <MDBCard className={"h-50"}>
                            <MDBCardBody>
                                <MDBValidation className='row g-3'>
                                    <MDBValidationItem className='col-md-6' feedback={""}>
                                        <MDBInput
                                            label='Mật khẩu hiện tại'
                                            name='fname'
                                            id='validationCustom01'
                                            required className="form-control"
                                            type='password'
                                            onChange={e => {
                                                setCurentPassword(e.target.value)
                                                validateForm()
                                            }}

                                        />
                                    </MDBValidationItem>
                                    <MDBValidationItem className='col-md-6' feedback={""}>
                                        <MDBInput
                                            label='Mật khẩu'
                                            name='fname'
                                            id='validationCustom01'
                                            required className="form-control"
                                            type='password'
                                            onChange={e => {
                                                setPassword(e.target.value)
                                                validateForm()
                                            }}
                                        />
                                    </MDBValidationItem>
                                    <MDBValidationItem className='col-md-6' feedback={""}>
                                        <MDBInput
                                            name='lname'
                                            id='validationCustom02'
                                            required className="form-control"
                                            label='Xác nhận mật khẩu'
                                            type='password'
                                            onChange={e => {
                                                setConfirmPassword(e.target.value)
                                                validateForm()
                                            }}

                                        />
                                    </MDBValidationItem>
                                    <div className='col-6 mt-3'>
                                        <button className={"btn btn-primary"} onClick={handleLogin}>Đổi mật khẩu
                                        </button>
                                    </div>
                                    <p hidden={notify === ''} className={'text-danger ml-2 mt-0'}>{notify}</p>
                                    <p hidden={!successMessage}
                                       className={'text-success ml-2 mt-0'}>{successMessage}</p>
                                </MDBValidation>
                            </MDBCardBody>
                        </MDBCard>
                    </MDBCol>

                </MDBRow>
            </MDBContainer>
            <ToastContainer />
        </section>

    );
}

export default Profile