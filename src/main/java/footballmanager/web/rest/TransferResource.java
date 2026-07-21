package footballmanager.web.rest;

import footballmanager.domain.Transfer;
import footballmanager.service.TransferService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferResource {

    private final TransferService transferService;

    public TransferResource(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    public List<Transfer> getAll() {
        return transferService.findAll();
    }

    @GetMapping("/{id}")
    public Transfer get(@PathVariable Long id) {
        return transferService.findOne(id);
    }

    @PostMapping
    public Transfer create(@RequestBody Transfer transfer) {
        return transferService.save(transfer);
    }

    @PutMapping("/{id}")
    public Transfer update(@PathVariable Long id, @RequestBody Transfer transfer) {
        transfer.setId(id);
        return transferService.update(transfer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        transferService.delete(id);
    }
}
