package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.ReviewNotFoundException;
import es.fjrj3d.seat_booker_api.models.Review;
import es.fjrj3d.seat_booker_api.repositories.IReviewRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    IReviewRepository iReviewRepository;

    public Review createReview(@Valid Review review) {
        return iReviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return iReviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return iReviewRepository.findById(id);
    }

    public Review updateReview(Review review, Long id) {
        if (!iReviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found with ID: " + id);
        }
        review.setId(id);
        return iReviewRepository.save(review);
    }

    public boolean deleteReview(Long id) {
        if (!iReviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found with ID: " + id);
        } else {
            iReviewRepository.deleteById(id);
            return true;
        }
    }
}
