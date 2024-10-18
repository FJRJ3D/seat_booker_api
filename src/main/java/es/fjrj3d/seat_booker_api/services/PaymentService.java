package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.repositories.IPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private IPaymentRepository iPaymentRepository;
}
