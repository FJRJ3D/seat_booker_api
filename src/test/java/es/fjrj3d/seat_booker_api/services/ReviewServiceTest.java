package es.fjrj3d.seat_booker_api.services;

import es.fjrj3d.seat_booker_api.exceptions.ReviewNotFoundException;
import es.fjrj3d.seat_booker_api.models.Review;
import es.fjrj3d.seat_booker_api.repositories.IReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewServiceTest {

    @Mock
    private IReviewRepository iReviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private Long reviewId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        review = new Review();
        review.setId(reviewId);
    }

    @Test
    public void test_create_review() {
        when(iReviewRepository.save(review)).thenReturn(review);

        Review createdReview = reviewService.createReview(review);

        assertEquals(review, createdReview);
        verify(iReviewRepository, times(1)).save(review);
    }

    @Test
    public void test_get_all_reviews() {
        when(iReviewRepository.findAll()).thenReturn(List.of(review));

        List<Review> reviews = reviewService.getAllReviews();

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(iReviewRepository, times(1)).findAll();
    }

    @Test
    public void test_get_review_by_id_found() {
        when(iReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Optional<Review> foundReview = reviewService.getReviewById(reviewId);

        assertTrue(foundReview.isPresent());
        assertEquals(review, foundReview.get());
        verify(iReviewRepository, times(1)).findById(reviewId);
    }

    @Test
    public void test_get_review_by_id_not_found() {
        when(iReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Optional<Review> foundReview = reviewService.getReviewById(reviewId);

        assertFalse(foundReview.isPresent());
        verify(iReviewRepository, times(1)).findById(reviewId);
    }

    @Test
    public void test_update_review_found() {
        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        when(iReviewRepository.existsById(reviewId)).thenReturn(true);
        when(iReviewRepository.save(updatedReview)).thenReturn(updatedReview);

        Review result = reviewService.updateReview(updatedReview, reviewId);

        assertEquals(updatedReview, result);
        verify(iReviewRepository, times(1)).existsById(reviewId);
        verify(iReviewRepository, times(1)).save(updatedReview);
    }

    @Test
    public void test_update_review_not_found() {
        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        when(iReviewRepository.existsById(reviewId)).thenReturn(false);

        ReviewNotFoundException thrown = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(updatedReview, reviewId);
        });

        assertEquals("Review not found with ID: " + reviewId, thrown.getMessage());
        verify(iReviewRepository, times(1)).existsById(reviewId);
    }

    @Test
    public void test_delete_review() {
        when(iReviewRepository.existsById(reviewId)).thenReturn(true);

        boolean result = reviewService.deleteReview(reviewId);

        assertTrue(result);
        verify(iReviewRepository, times(1)).existsById(reviewId);
        verify(iReviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    public void test_delete_review_not_found() {
        when(iReviewRepository.existsById(reviewId)).thenReturn(false);

        ReviewNotFoundException thrown = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId);
        });

        assertEquals("Review not found with ID: " + reviewId, thrown.getMessage());
        verify(iReviewRepository, times(1)).existsById(reviewId);
    }
}