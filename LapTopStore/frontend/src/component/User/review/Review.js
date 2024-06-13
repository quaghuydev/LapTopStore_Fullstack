import React, {useEffect, useState} from 'react';
import axios from 'axios';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {Link} from "react-router-dom";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTrash} from "@fortawesome/free-solid-svg-icons";
import {Button, Modal} from "react-bootstrap";
import RootPathApi from "../../../route/RootPathApi";

function Review({productId}) {
    const [reviews, setReviews] = useState([]);
    const [showMore, setShowMore] = useState(false);
    const [visibleReviews, setVisibleReviews] = useState(3); // Số lượng đánh giá hiển thị
    const [lastAdded, setLastAdded] = useState(0); // Số đánh giá được thêm vào lần trước
    const [newReview, setNewReview] = useState({rate:1, content: '',email:'',productId:productId, quality:'GOOD',});

    const [isSubmittingReview, setIsSubmittingReview] = useState(false);
    const userJson = sessionStorage.getItem("user");
    const user = userJson ? JSON.parse(userJson) : null; // Kiểm tra xem userJson có giá trị null không
    const em = user ? user.email : ""; // Lấy email nếu user tồn tại, ngược lại gán chuỗi rỗng
    const [showConfirmationModal, setShowConfirmationModal] = useState(false);
    const [idToDelete, setIdToDelete] = useState(0);
    const baseUrl =RootPathApi()

    console.log(": "+reviews);
    useEffect(() => {
        // Lấy danh sách đánh giá từ server
        axios.get(`http://localhost:8080/api/v1/review/all?product_id=${productId}`)
            .then(response => {
                setReviews(response.data);
                // console.log(response.data);
            })
            .catch(error => {
                    console.error('Có lỗi xảy ra khi lấy đánh giá:', error);
                }
            );
    }, [productId]);


    const handleSubmit = (event) => {
        event.preventDefault();
        const quality = newReview.rate < 3 ? "BAD" : "GOOD";

        setIsSubmittingReview(true);
        axios.post(`http://localhost:8080/api/v1/review/create`, {
            rate:newReview.rate,
            email:em,
            quality:quality,
            content:newReview.content,
            productId:productId
        })
            .then(response => {


                const newReviewData = response.data; // Lấy dữ liệu đánh giá mới
                setIsSubmittingReview(false);
                setNewReview(newReviewData);
                console.log(newReviewData);

                if (newReviewData.id === -999||newReviewData.id === 0) {

                    toast.error("Bạn chưa mua sản phẩm!");

                }else {
                    setReviews([newReviewData, ...reviews]);
                    setNewReview({ rate: 1, content: '', email: '', productId: productId, quality: quality });

                    toast.success("Đánh giá của bạn đã được gửi thành công!");
                }





            })
            .catch(error => {
                toast.error("Bạn chưa mua sản phẩm!");
                console.error('Có lỗi xảy ra khi gửi đánh giá:', error);
            });
        // Gửi đánh


    };

    const handleChange = (event) => {
        const {name, value} = event.target;
        setNewReview(prevReview => ({
            ...prevReview,
            [name]: value
        }));
    };
    const handleShowMore = () => {
        setVisibleReviews(prevVisibleReviews => prevVisibleReviews + 3);
    };

    const handleShowLess = () => {
        setVisibleReviews(3); // Đặt visibleReviews về 3 khi nhấn "Ẩn bớt"
    };
    console.log(reviews);
    const averageRating = reviews.length > 0 ? (
        reviews.reduce((sum, review) => sum + review.rate, 0) / reviews.length
    ) : 0;

    // Sử dụng useEffect để gửi điểm trung bình lên cha
    useEffect(() => {
        if (typeof window !== 'undefined' && window.dispatchEvent) {
            const event = new CustomEvent('averageRatingCalculated', { detail: { averageRating } });
            window.dispatchEvent(event);
        }
    }, [averageRating]);
    console.log(averageRating);

    // const handleDelete = async (reviewId) => {
    //     if (window.confirm("Bạn có chắc chắn muốn xóa đánh giá này?")) {
    //         try {
    //             const response = await axios.delete(`http://localhost:8080/api/v1/review/delete?id=${reviewId}`);
    //
    //             if (response.status === 202) { // HttpStatus.ACCEPTED là 202
    //                 setReviews(prevReviews => prevReviews.filter(r => r.id !== reviewId));
    //                 toast.success("Đánh giá đã được xóa thành công!");
    //             } else {
    //                 toast.error(`Xóa đánh giá không thành công! (Mã lỗi: ${response.status})`);
    //             }
    //         } catch (error) {
    //             console.error('Có lỗi xảy ra khi xóa đánh giá:', error);
    //             toast.error("Xóa đánh giá không thành công! (Lỗi mạng)");
    //         }
    //     }
    // };
    const handleDeleteClick = (reviewId) => {
        setIdToDelete(reviewId);
        setShowConfirmationModal(true);
    };

    const handleDeleteConfirm = async () => {
        try {
            const response = await axios.delete(`http://localhost:8080/api/v1/review/delete?id=${idToDelete}`);

            if (response.status === 202) {
                setReviews(prevReviews => prevReviews.filter(r => r.id !== idToDelete));
                toast.success("Đánh giá đã được xóa thành công!");
            } else {
                toast.error(`Xóa đánh giá không thành công! (Mã lỗi: ${response.status})`);
            }
        } catch (error) {
            console.error('Có lỗi xảy ra khi xóa đánh giá:', error);
            toast.error("Xóa đánh giá không thành công! (Lỗi mạng)");
        } finally {
            setShowConfirmationModal(false); // Đóng modal sau khi xử lý
        }
    };
    const handleReviewSubmit = (newReview) => {

    }
    console.log(reviews)



    return (
        <div id="review" className="tab-pane fade">

            <div className="group-title">
                <h2>Đánh giá của khách hàng ({reviews.length} Đánh giá)</h2>
            </div>

            <div className="review-list" style={{display: "flex", flexDirection: "column", gap: "20px",}}>
                {reviews.length > 0 ? (
                    [...reviews].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, visibleReviews).map((review, index) => (
                        <div key={review.id} className="review-item" style={{
                            border: "1px solid #ccc",
                            padding: "15px",
                            borderRadius: "5px",
                        }}>
                            {review.user && review.user.email &&

                                <p> {review.user.email}</p>}
                            {review.user && user && user.email === review.user.email && (
                                <div style={{textAlign: 'right'}}> {/* Nút xóa */}
                                    <button type="button" className="delete-btn btn-lg"
                                            onClick={() => handleDeleteClick(review.id)}
                                            style={{float: 'right', alignItems: 'center'}}>
                                        <FontAwesomeIcon icon={faTrash}/>
                                    </button>
                                </div>
                            )}
                            <div className="rating">

                                {[...Array(5)].map((star, i) => (
                                    <i
                                        key={i}
                                        className={`fa fa-star${i < review.rate ? "" : "-o"}`}
                                    ></i>
                                ))}
                            </div>
                            <p className="text-muted">Ngày đánh giá: {review.createdAt} | Danh mục sản phẩm: {review.product.category.value}</p>

                            <p>Chất lượng: {review.quality === "BAD" ? "Không tốt" : "Tốt"}</p>

                            <p>Nội dung: {review.content}</p>

                        </div>

                    ))) : (
                    <p>Không có dữ liệu</p> // Hiển thị thông báo khi không có đánh giá
                )}
                <div style={{display: "flex", gap: "10px"}}>
                    {visibleReviews < reviews.length && (
                        <button onClick={() => {
                            const added = Math.min(3, reviews.length - visibleReviews);
                            setVisibleReviews(prev => prev + added);
                            setLastAdded(added);
                        }} className="customer-btn">
                            Xem thêm
                        </button>
                    )}
                    {visibleReviews > 3 && (
                        <button onClick={handleShowLess} className="customer-btn">
                            Ẩn bớt
                        </button>
                    )}
                </div>
            </div>

            {/*{user ?(*/}
            {/*<div className="review border-default universal-padding mt-30">*/}

            {/*    <h2 className="review-title mb-30">Bạn đang đánh giá:</h2>*/}
            {/*    <p className="review-mini-title">Your rating</p>*/}
            {/*    <ul className="review-list">*/}
            {/*        {[...Array(5)].map((star, i) => (*/}
            {/*            <i key={i} className={`fa fa-star${i < newReview.rate ? '' : '-o'}`}*/}
            {/*               onClick={() => setNewReview({...newReview, rate: i + 1})}></i>*/}
            {/*        ))}*/}
            {/*    </ul>*/}

            {/*    <div className="review-field mt-40">*/}
            {/*        <form autoComplete="off" onSubmit={handleSubmit}>*/}

            {/*        <div className="form-group">*/}
            {/*            <label className="req" htmlFor="content">Đánh giá</label>*/}
            {/*                <textarea className="form-control" rows="5" id="content" name="content"*/}
            {/*                          value={newReview.content} onChange={handleChange} required></textarea>*/}
            {/*            </div>*/}
            {/*            <button type="submit" className="customer-btn">Gửi</button>*/}
            {/*        </form>*/}
            {/*    </div>*/}
            {/*</div>*/}
            {/*): (*/}
            {/*    <div className="review border-default universal-padding mt-30 text-center">*/}
            {/*        <p className="text-center fs-2 mt-4"> Vui lòng{" "}*/}
            {/*            <Link to="/login" className="text-primary">*/}
            {/*                đăng nhập*/}
            {/*            </Link>{" "}*/}
            {/*            để đánh giá.</p>*/}
            {/*    </div>*/}
            {/*)}*/}
            <ToastContainer/>
            <Modal show={showConfirmationModal} onHide={() => setShowConfirmationModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Xóa đánh giá</Modal.Title>
                </Modal.Header>
                <Modal.Body>Bạn có chắc chắn muốn xóa đánh giá này?</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowConfirmationModal(false)}>
                        Hủy
                    </Button>
                    <Button variant="danger" onClick={handleDeleteConfirm}>
                        Xóa
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Review;