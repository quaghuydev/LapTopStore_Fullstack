package vn.id.quanghuydevfs.drcomputer.controller.evaluate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.id.quanghuydevfs.drcomputer.dto.evaluate.EvaluateRequest;
import vn.id.quanghuydevfs.drcomputer.model.evaluate.Evaluate;
import vn.id.quanghuydevfs.drcomputer.model.order.Order;
import vn.id.quanghuydevfs.drcomputer.model.order.OrderItem;
import vn.id.quanghuydevfs.drcomputer.model.user.User;
import vn.id.quanghuydevfs.drcomputer.repository.*;
import vn.id.quanghuydevfs.drcomputer.service.AuthService;
import vn.id.quanghuydevfs.drcomputer.service.EvaluateService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/evaluate")
@CrossOrigin("*")
public class EvaluateController {
    private final EvaluateService service;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired

    private EvaluateRepository evaluateRepository;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Evaluate>> getAll() {
        return ResponseEntity.ok(service.getEvaluates());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Optional<Evaluate>> getById(long id) {
        return ResponseEntity.ok(service.getEvaluateById(id));
    }

    @PostMapping("/post")
    public ResponseEntity<String> createEvalue(@RequestBody EvaluateRequest evl) {
        Optional<User> u = userRepository.findByEmail(evl.getEmail());
        Order op = orderRepository.findByIdT(evl.getOrder_id());

        try {
            Optional<Evaluate> evaluate = evaluateRepository.findByOrder(op.getId());
            if (evaluate.isPresent()) {
                evaluate.get().setContent(evl.getContent());
                evaluate.get().setRate(evl.getRate());
                evaluate.get().setQuality(evl.getQuality());
                evaluateRepository.save(evaluate.get());
                return ResponseEntity.ok("da danh gias");
            }
            if (!u.isPresent()) {
                return ResponseEntity.ok("false");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        Evaluate evl1 = new Evaluate();
        evl1.setContent(evl.getContent());
        evl1.setUser(u.get());
        evl1.setOrder(op);
        evl1.setQuality(evl.getQuality());
        evl1.setReply(evl.getReply());
        evl1.setRate(evl.getRate());
        evl1.setRoles(evl.getRole());
        Evaluate save = service.post(evl1);
        return ResponseEntity.ok("true");
    }

    @GetMapping("/product")
    public ResponseEntity<List<OrderItem>> geteluateByIdProduct(@RequestParam Long id) {
        List<OrderItem> res = orderRepository.findByIdT(id).getOrderItems();
        return ResponseEntity.ok(res);
    }


}
