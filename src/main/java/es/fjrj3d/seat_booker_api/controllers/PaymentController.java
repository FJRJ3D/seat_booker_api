package es.fjrj3d.seat_booker_api.controllers;

import com.stripe.exception.StripeException;
import es.fjrj3d.seat_booker_api.dtos.PaymentDTO;
import es.fjrj3d.seat_booker_api.dtos.TransactionDTO;
import es.fjrj3d.seat_booker_api.models.Seat;
import es.fjrj3d.seat_booker_api.models.User;
import es.fjrj3d.seat_booker_api.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/add-payment-method")
    public ResponseEntity<PaymentDTO> addPaymentMethod(@RequestBody Map<String, String> request) {
        String paymentMethodId = request.get("paymentMethodId");

        User user = paymentService.getUserFromAuthentication();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String customerId = user.getStripeCustomerId();
        PaymentDTO paymentDTO = paymentService.addPaymentMethod(customerId, paymentMethodId, user);

        if (paymentDTO != null) {
            return ResponseEntity.ok(paymentDTO);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/charge-seat/{seatId}/{paymentMethodId}")
    public ResponseEntity<?> processSeatPayment(@PathVariable Long seatId, @PathVariable String paymentMethodId) {
        User user = paymentService.getUserFromAuthentication();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Seat seat = paymentService.checkSeatAvailability(seatId);
        if (seat == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Seat not available");
        }

        try {
            TransactionDTO transactionDTO = paymentService.processSeatPayment(user, seat, paymentMethodId);
            return ResponseEntity.ok(transactionDTO);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed: "
                    + e.getMessage());
        }
    }
}
