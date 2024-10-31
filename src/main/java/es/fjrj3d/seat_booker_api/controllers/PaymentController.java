package es.fjrj3d.seat_booker_api.controllers;

import es.fjrj3d.seat_booker_api.models.Payment;
import es.fjrj3d.seat_booker_api.models.User;
import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import es.fjrj3d.seat_booker_api.services.PaymentService;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private IUserRepository iUserRepository;

    @PostMapping("/add-payment-method")
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody Payment request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = iUserRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String customerId = user.getStripeCustomerId();
        PaymentMethod paymentMethod = paymentService.addPaymentMethod(customerId, request);

        if (paymentMethod != null) {
            return ResponseEntity.ok(paymentMethod);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
