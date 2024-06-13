package vn.id.quanghuydevfs.drcomputer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.quanghuydevfs.drcomputer.model.evaluate.Evaluate;
import vn.id.quanghuydevfs.drcomputer.repository.EvaluateRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluateService {

    private final EvaluateRepository repository;


    public List<Evaluate> getEvaluates() {
        return repository.findAll();
    }

    public Optional<Evaluate> getEvaluateById(Long id) {
        return repository.findById(id);
    }

    public Evaluate post(Evaluate evaluate) {
        var e = Evaluate.builder()
                .user(evaluate.getUser())
                .order(evaluate.getOrder())
                .content(evaluate.getContent())
                .rate(evaluate.getRate())
                .quality(evaluate.getQuality())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
        repository.save(e);

        return e;
    }


    public boolean reply(long id, String replyText) {
        var evaluate = repository.findById(id).orElse(null);
        if (evaluate != null) {
            evaluate.setReply(replyText);
            evaluate.setUpdatedAt(LocalDate.now());
            repository.save(evaluate);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteEvaluateById(long id) {
        var e = repository.findById(id).orElse(null);
        if (e != null) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
