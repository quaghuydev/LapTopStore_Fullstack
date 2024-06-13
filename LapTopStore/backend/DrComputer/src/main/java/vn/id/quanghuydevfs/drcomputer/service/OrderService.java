package vn.id.quanghuydevfs.drcomputer.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.id.quanghuydevfs.drcomputer.controller.order.OrderResponse;
import vn.id.quanghuydevfs.drcomputer.dto.order.OrderDto;
import vn.id.quanghuydevfs.drcomputer.dto.product.ProductItemDto;
import vn.id.quanghuydevfs.drcomputer.exception.ResourceNotFoundException;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItemPK;
import vn.id.quanghuydevfs.drcomputer.model.order.StatusOrder;
import vn.id.quanghuydevfs.drcomputer.model.product.Product;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.OrderDetailRepository;
import vn.id.quanghuydevfs.drcomputer.repository.OrderRepository;
import vn.id.quanghuydevfs.drcomputer.repository.ProductRepository;
import vn.id.quanghuydevfs.drcomputer.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service

@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final OrderDetailRepository detailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private boolean isScheduledActive = true;

    public List<Order> getOrders() {
        return repository.findAll();
    }

    @Transactional
    public OrderResponse createOrder(OrderDto orderDto) throws ResourceNotFoundException {
        BigDecimal totalAmount = BigDecimal.ZERO;
        User user = userRepository.findByEmail(orderDto.getUser().getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = Order.builder()
                .id(orderDto.getId())
                .fullname(orderDto.getFullname())
                .user(user)
                .street(orderDto.getStreet())
                .ward(orderDto.getWard())
                .district(orderDto.getDistrict())
                .province(orderDto.getProvince())
                .note(orderDto.getNote())
                .numberHouse(orderDto.getNumberHouse())
                .isPaied(orderDto.isPaied())
                .paymentMethod(orderDto.getPayMethod())
                .status(StatusOrder.pending)
                .createdAt(LocalDate.now())
                .rewardPoints(orderDto.getRewardPoints())
                .orderItems(new ArrayList<>())
                .updatedAt(LocalDate.now())
                .build();

        // Save the Order immediately to ensure it has an ID.
        order = repository.save(order);

        for (ProductItemDto p : orderDto.getProducts()) {
            Product product = productRepository.findById(p.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            OrderItem orderItem = OrderItem.builder()
                    .id(new OrderItemPK(order.getId(), product.getId()))
                    .order(order)
                    .product(product)
                    .price((product.getPrice() - (product.getSale() / 100) * product.getPrice()) * p.getQuantity())
                    .quantity(p.getQuantity())
                    .build();

            order.addOrderItem(orderItem);

            totalAmount = totalAmount.add(
                    BigDecimal.valueOf(
                            calculatePriceAfterSale(product.getSale(), product.getPrice() * p.getQuantity(), order.getRewardPoints())
                    )
            );
        }

        order.setTotalAmount(totalAmount);
        order = repository.save(order);
        user.setRewardPoints(user.getRewardPoints() + 1-order.getRewardPoints());
        userRepository.save(user);
        return OrderResponse.builder()
                .user(orderDto.getUser())
                .order(order)
                .build();// Save the Order again, now with its OrderItems.
    }


    public static long calculatePriceAfterSale(double sale, long price, long rewardPoints) {
        return (long) (price - price * (sale / 100) - (100 * rewardPoints));
    }

    public Order getOrderById(long id) {
        return repository.findById(id).orElse(null);
    }

    public Boolean delete(long id) {
        var order = repository.findById(id).orElse(null);

        if (order != null) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void deleteMultiple(List<Long> ids) {
        for (Long id : ids) {
            repository.findById(id).ifPresent(order -> repository.deleteById(id));
        }
    }

    public Integer countOrdersByMonthAndYear(int month, int year) {
        return repository.countOrdersByMonthAndYear(month, year);
    }

    public BigDecimal getTotalRevenueByYear(int year) {
        return repository.findTotalRevenueByYear(year);
    }

    public List<Integer> getMonth() {
        return repository.getMonthInOrder();
    }

    public List<Integer> getMonthInYear(int year) {
        return repository.getMonthInYearOrder(year);
    }

    public List<Integer> getYear() {
        return repository.getYearInOrder();
    }

    public Integer countOrderInYear(int year) {
        return repository.countInYear(year);
    }

    public List<Product> getBestSellingProductsByMonthAndYear(int month, int year) {
        List<Object[]> results = repository.findBestSellingProductsByMonthAndYear(month, year);
        return results.stream()
                .map(result -> (Product) result[0])
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByStatus(String status, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (status == null || status.equalsIgnoreCase("all")) {
                return repository.findAll();
            }
            try {
                StatusOrder statusOrder = StatusOrder.valueOf(status.toLowerCase());
                return repository.findByStatusAndUser(statusOrder, user.get());
            } catch (IllegalArgumentException e) {
                return repository.findAllByUser(user.get());
            }

        } else {
            return null;
        }
    }

    public List<Order> getOrdersByUserEmail(String email) {
        return repository.findAllByUserEmail(email);
    }

    public boolean cancelOrder(long id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            order.get().setStatus(StatusOrder.cancel);
            repository.save(order.get());
            return true;
        }
        return false;
    }

    public boolean returnOrder(long id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            order.get().setStatus(StatusOrder.returned);
            repository.save(order.get());
            return true;
        }
        return false;
    }

    public boolean confirmOrder(long id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            order.get().setStatus(StatusOrder.picking);
            repository.save(order.get());
            checkAndStartScheduler();
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 300000) // 300000 ms = 5 minutes
    public void updateOrderStatus() {
        if (!isScheduledActive) {
            return;
        }

        List<Order> orders = repository.findAll()
                .stream()
                .filter(order -> order.getStatus() != null)
                .toList();

        boolean allDelivered = true;
        for (Order order : orders) {
            if (!order.getStatus().equals(StatusOrder.delivered)) {
                allDelivered = false;
                StatusOrder nextStatus = getNextStatus(order.getStatus());
                if (nextStatus != null) {
                    order.setStatus(nextStatus);
                    repository.save(order);
                }
            }
        }

        if (allDelivered) {
            isScheduledActive = false;
        }
    }

    public void checkAndStartScheduler() {
        List<Order> orders = repository.findAll();
        for (Order order : orders) {
            if (order.getStatus().equals(StatusOrder.picking)) {
                isScheduledActive = true;
                break;
            }
        }
    }

    @Transactional
    public void confirmOrders(List<Long> ids) {
        for (Long id : ids) {
            Optional<Order> order = repository.findById(id);
            if (order.isPresent()) {
                order.get().setStatus(StatusOrder.picking);
                repository.save(order.get());
                checkAndStartScheduler();
            }
        }
    }

    private StatusOrder getNextStatus(StatusOrder currentStatus) {
        return switch (currentStatus) {
            case picking -> StatusOrder.transporting;
            case transporting -> StatusOrder.delivering;
            case delivering -> StatusOrder.delivered;
            default -> null;
        };
    }

//    public static void main(String[] args) {
//        System.out.println(calculatePriceAfterSale(0.15, 50000));
//    }

}

