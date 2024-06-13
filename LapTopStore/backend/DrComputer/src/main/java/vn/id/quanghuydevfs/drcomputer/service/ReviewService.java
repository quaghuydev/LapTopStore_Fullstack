package vn.id.quanghuydevfs.drcomputer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.quanghuydevfs.drcomputer.dto.evaluate.ReviewRequest;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.model.review.Review;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.OrderItemRepository;
import vn.id.quanghuydevfs.drcomputer.repository.ProductRepository;
import vn.id.quanghuydevfs.drcomputer.repository.ReviewRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    OrderItemRepository orderItemRepository;


    public Review createReview(ReviewRequest req, User user) throws Exception {
        Review review = new Review();
        Optional<Product> product = productRepository.findById(req.getProductId());

        // get orrder id from product
        List<OrderItem> listOrderItem = orderItemRepository.getOrderItemByProductId(req.getProductId());
        if(listOrderItem.isEmpty()){
            return new Review();
        }
        List<Order> listOrder = listOrderItem.stream().map(OrderItem::getOrder)
                .toList();


        // get usser ud from order
        List<Long> userIds = listOrder.stream()
                .map(order ->
                        order.getUser().getId())
                .toList();
        // compare user out vs usser in
        boolean contains = userIds.contains(user.getId());
        if(!contains){
            Review r=new Review();
            r.setId(-999);
            return r;
        }


//        Review check = reviewRepository.checkExist(product.get().getId(), user.getId());
//        if (check != null) {
//            return new Review();
//        }

        review.setUser(user);
        review.setProduct(product.get());
        review.setContent(req.getContent());
        review.setQuality(req.getQuality());
        review.setRate(req.getRate());
        review.setCreatedAt(LocalDate.now());
        Review save = reviewRepository.save(review);


        return save;
    }

    public List<Review> getAllReviewByProduct(Long productId) throws Exception {
        List<Review> tb = reviewRepository.getALlByIdProduct(productId);
        if (tb.isEmpty()) {
            return null;
        }
        return tb;
    }
    public List<Review> getAll(){
        return reviewRepository.findAll();
    }
    public String deleteReview(Long id){
        Optional<Review> review=reviewRepository.findById(id);
        if (!review.isPresent()){
            return "Not found";
        }
        try {
            System.out.println(id);
            reviewRepository.delete(review.get());
            return "success";
        }catch (Exception ee){
            System.out.println(ee.getMessage());
            return "false";
        }

    }
    @Transactional
    public void deleteMultiple(List<Long> list) {
        for (Long id : list) {
            try {
                reviewRepository.deleteById(id);
            } catch (Exception e) {
                // Xử lý lỗi ở đây, ví dụ: log lỗi hoặc ném một ngoại lệ tùy chỉnh
                throw new RuntimeException("Error deleting review with ID " + id, e);
            }
        }
    }

}
