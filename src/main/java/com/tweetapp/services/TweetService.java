package com.tweetapp.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tweetapp.dto.Replay;
import com.tweetapp.dto.TweetResponse;
import com.tweetapp.exception.InvalidUsernameException;
import com.tweetapp.exception.TweetDoesNotExistException;
import com.tweetapp.model.Tweet;
import com.tweetapp.repositories.TweetRepository;
import com.tweetapp.utils.TweetConstants;

import io.micrometer.core.instrument.util.StringUtils;


@Log4j2
@Component("tweet-service")
public class TweetService {

	@Autowired
    private TweetRepository tweetRepository;


    public List<Tweet> getAllTweets() {
        log.debug("Found total : {} tweets", tweetRepository.findAll().size());
        return tweetRepository.findAll();
    }

    public List<TweetResponse> getUserTweets(final String username, final String loggedInUser) throws InvalidUsernameException {
        if (!StringUtils.isBlank(username)) {
            List<Tweet> tweets = tweetRepository.findByUsername(username);

            log.debug("found the total tweets: {} for user: {}", tweets.size(), username);
            return tweets.stream().map(tweet -> {
                final Integer likesCount = tweet.getLikes().size();
                final Boolean likeStatus = tweet.getLikes().contains(loggedInUser);
                final Integer commentsCount = tweet.getComments().size();
                return new TweetResponse(tweet.getTweetId(), username, tweet.getTweetText(), tweet.getFirstName(),
                        tweet.getLastName(), tweet.getTweetDate(), likesCount, commentsCount, likeStatus,
                        tweet.getComments());
            }).collect(Collectors.toList());
        } else {
            log.error(String.format("Username %s provided is invalid", username));
            throw new InvalidUsernameException("Username/loginId provided is invalid");
        }

    }

    public Tweet postNewTweet(final String username, final Tweet newTweet) {
        newTweet.setTweetId(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        log.debug("Posted new tweet for user : {} ", username);
        return tweetRepository.insert(newTweet);
    }

    public TweetResponse getTweet(final String tweetId, final String username) throws TweetDoesNotExistException {
        Optional<Tweet> tweetFounded = tweetRepository.findById(tweetId);
        if (tweetFounded.isPresent()) {
            final Tweet tweet = tweetFounded.get();
            final Integer likesCount = tweet.getLikes().size();
            final Boolean likeStatus = tweet.getLikes().contains(username);
            final Integer commentsCount = tweet.getComments().size();

            log.debug("found the tweet with Id: {} for user: {}", tweetId, username);
            return new TweetResponse(tweet.getTweetId(), tweet.getUsername(), tweet.getTweetText(),
                    tweet.getFirstName(), tweet.getLastName(), tweet.getTweetDate(), likesCount, commentsCount,
                    likeStatus, tweet.getComments());
        } else {
            log.error(String.format(TweetConstants.TWEET_DOES_NOT_EXISTS + "Current User is: %s", username));
            throw new TweetDoesNotExistException(TweetConstants.TWEET_DOES_NOT_EXISTS);
        }

    }

    public Tweet updateTweet(final String userId, final String tweetId, final String updatedTweetText) throws TweetDoesNotExistException {

        Optional<Tweet> originalTweetOptional = tweetRepository.findById(tweetId);
        if (originalTweetOptional.isPresent()) {
            Tweet tweet = originalTweetOptional.get();
            tweet.setTweetText(updatedTweetText);
            log.debug("updated tweet for user : {} ", userId);
            return tweetRepository.save(tweet);
        } else {
            log.error(String.format(TweetConstants.TWEET_DOES_NOT_EXISTS + "Current User is: %s", userId));
            throw new TweetDoesNotExistException(TweetConstants.TWEET_DOES_NOT_EXISTS);
        }

    }

    public boolean deleteTweet(final String tweetId) throws TweetDoesNotExistException {
        if (tweetRepository.existsById(tweetId) && !StringUtils.isBlank(tweetId)) {
            tweetRepository.deleteById(tweetId);
            log.debug("deleted tweet with tweetId : {} ", tweetId);
            return true;
        } else {
            log.error(String.format(TweetConstants.TWEET_ISSUE + "Current tweetId is: %s", tweetId));
            throw new TweetDoesNotExistException(TweetConstants.TWEET_DOES_NOT_EXISTS);
        }
    }

    public Tweet likeTweet(final String username, final String tweetId) throws TweetDoesNotExistException {
        Optional<Tweet> tweetOptional = tweetRepository.findById(tweetId);
        if (tweetOptional.isPresent()) {
            Tweet tweet = tweetOptional.get();
            tweet.getLikes().add(username);
            log.debug("liked tweet with tweetId : {} by user : {} ", tweetId, username);
            return tweetRepository.save(tweet);
        } else {
            log.error(String.format(TweetConstants.TWEET_ISSUE + "Current User is: %s", username));
            throw new TweetDoesNotExistException(TweetConstants.TWEET_DOES_NOT_EXISTS);
        }
    }

    public Tweet dislikeTweet(final String username, final String tweetId) throws TweetDoesNotExistException {
        Optional<Tweet> tweetOptional = tweetRepository.findById(tweetId);
        if (tweetOptional.isPresent()) {
            Tweet tweet = tweetOptional.get();
            tweet.getLikes().remove(username);
            return tweetRepository.save(tweet);
        } else {
            log.error(String.format(TweetConstants.TWEET_ISSUE + "Current User is: %s", username));
            throw new TweetDoesNotExistException(TweetConstants.TWEET_DOES_NOT_EXISTS);
        }
    }

    public Tweet replyTweet(final String username, final String tweetId, final String tweetReply) throws TweetDoesNotExistException {
        Optional<Tweet> tweetOptional = tweetRepository.findById(tweetId);
        if (tweetOptional.isPresent()) {
            Tweet tweet = tweetOptional.get();
            tweet.getComments().add(new Replay(username, tweetReply));
            return tweetRepository.save(tweet);
        } else {
            log.error(String.format(TweetConstants.TWEET_ISSUE + "Current User is: %s", username));
            throw new TweetDoesNotExistException(TweetConstants.TWEET_DOES_NOT_EXISTS);
        }
    }

}
