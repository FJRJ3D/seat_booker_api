package es.fjrj3d.seat_booker_api.services;

import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodAttachParams;
import es.fjrj3d.seat_booker_api.dtos.PaymentMethodDTO;
import es.fjrj3d.seat_booker_api.models.Payment;
import es.fjrj3d.seat_booker_api.models.User;
import es.fjrj3d.seat_booker_api.repositories.IPaymentRepository;
import es.fjrj3d.seat_booker_api.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository iPaymentRepository;

    @Autowired
    private IUserRepository iUserRepository;

    public User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return iUserRepository.findByEmail(email).orElse(null);
    }

    public PaymentMethodDTO addPaymentMethod(String customerId, String paymentMethodId, User user) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.attach(PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build());

            Payment payment = new Payment();
            payment.setPaymentMethodToken(paymentMethodId);
            payment.setPaymentMethodType(paymentMethod.getType());
            payment.setPaymentMethodId(paymentMethod.getId());
            payment.setUser(user);
            iPaymentRepository.save(payment);

            return new PaymentMethodDTO(paymentMethod.getId(), paymentMethod.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
