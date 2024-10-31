package es.fjrj3d.seat_booker_api.services;

import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import es.fjrj3d.seat_booker_api.models.Payment;
import es.fjrj3d.seat_booker_api.repositories.IPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository iPaymentRepository;

    public PaymentMethod addPaymentMethod(String customerId, Payment request) {

        PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(PaymentMethodCreateParams.CardDetails.builder()
                        .setNumber(request.getCardNumber())
                        .setExpMonth(request.getExpMonth())
                        .setExpYear(request.getExpYear())
                        .setCvc(request.getCvc())
                        .build())
                .build();

        try {
            PaymentMethod paymentMethod = PaymentMethod.create(params);

            paymentMethod.attach(PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build());

            Payment payment = new Payment();
            payment.setCardNumber(request.getCardNumber());
            payment.setExpMonth(request.getExpMonth());
            payment.setExpYear(request.getExpYear());
            payment.setCvc(request.getCvc());

            iPaymentRepository.save(payment);

            return paymentMethod;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
