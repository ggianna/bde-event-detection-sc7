/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.crawlers.twitter;

import gr.demokritos.iit.crawlers.twitter.factory.Configuration;
import gr.demokritos.iit.crawlers.twitter.policy.DefensiveCrawlPolicy;
import gr.demokritos.iit.crawlers.twitter.policy.ICrawlPolicy;
import gr.demokritos.iit.crawlers.twitter.repository.IRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author George K. <gkiom@iit.demokritos.gr>
 */
public abstract class AbstractTwitterListener {

    protected final String twitterConsumerKey;
    protected final String twitterConsumerKeySecret;
    protected final String twitterAccessTokken;
    protected final String twitterAccessTokkenSecret;

    protected IRepository repository;

    // will extract URLs from the tweet, if any
//    protected Extractor extractor;
    // twitter-text lib is a maven snapshot build at 16/05/14
    // cloned from https://github.com/twitter/twitter-text-java.git
    protected Twitter twitter;
    protected ICrawlPolicy policy;

    /**
     * Main constructor. Accepts a configuration class that has already read
     * resources. Default CrawlingPolicy=Defensive
     *
     * @param config the configuration class
     * @param repository
     */
    public AbstractTwitterListener(Configuration config, IRepository repository) {
        // init credentials
        this.twitterConsumerKey = config.getTwitterConsumerKey();
        this.twitterConsumerKeySecret = config.getTwitterConsumerKeySecret();
        this.twitterAccessTokken = config.getTwitterAccessTokken();
        this.twitterAccessTokkenSecret = config.getTwitterAccessTokkenSecret();
        this.repository = repository;
        //connect
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterConsumerKey)
                .setOAuthConsumerSecret(twitterConsumerKeySecret)
                .setOAuthAccessToken(twitterAccessTokken)
                .setOAuthAccessTokenSecret(twitterAccessTokkenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        // get active instance
        this.twitter = tf.getInstance();
        this.policy = new DefensiveCrawlPolicy();
    }

    /**
     * Alternate constructor. Accepts a configuration class that has already
     * read resources.
     *
     * @param config the configuration class
     * @param repository
     * @param policy
     */
    public AbstractTwitterListener(Configuration config, IRepository repository, ICrawlPolicy policy) {
        // init credentials
        this.twitterConsumerKey = config.getTwitterConsumerKey();
        this.twitterConsumerKeySecret = config.getTwitterConsumerKeySecret();
        this.twitterAccessTokken = config.getTwitterAccessTokken();
        this.twitterAccessTokkenSecret = config.getTwitterAccessTokkenSecret();
        this.repository = repository;
        this.policy = policy;
        //connect
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterConsumerKey)
                .setOAuthConsumerSecret(twitterConsumerKeySecret)
                .setOAuthAccessToken(twitterAccessTokken)
                .setOAuthAccessTokenSecret(twitterAccessTokkenSecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        // get active instance
        this.twitter = tf.getInstance();
    }

    protected List<Status> processStatuses(List<Status> statuses, IRepository.CrawlEngine engine_type, long engine_id) {
        List<Status> res = new ArrayList();
        // for each status
        for (Status status : statuses) {
            // if it is a retweet, get the original tweet
            while (status.isRetweet()) {
                status = status.getRetweetedStatus();
            }
            // add status to result list (avoid retweets)
            res.add(status);
            // proceed with storing in twitter repository
            long postID = status.getId();
            User user = status.getUser();
            // check for existance of post in DB
            boolean exists = repository.existsPost(postID);
            // if post already in the db then update post and user info
            if (exists) {
                repository.updatePost(status);
                repository.updateUser(user);
            } else {
                // get User ID
                long userID = user.getId();
                // check if user exists in the DB
                boolean exists_user = repository.existsUser(userID);
                String sourceAcc = user.getScreenName();
                if (exists_user) {
                    // if user is in the database, update
                    repository.updateUser(user);
                } else {
                    // else insert
                    repository.insertUser(user);
                }
                // get source ID
                if (!repository.existSource(sourceAcc)) {
                    // also insert as a source
                    repository.saveAccount(sourceAcc, false);
                }
                // get followers of user when post was published
                int followersWhenPublished = user.getFollowersCount();
                // finally, insert the post in the DB
                repository.insertPost(status, userID, user.getScreenName(), followersWhenPublished, engine_type, engine_id);
            }
        }
        return res;
    }

    /**
     * TODO: implement a method that checks rate limit status and stops crawl if
     * reached.
     *
     * @param sKey the REST call to check
     * @throws java.lang.InterruptedException
     * @throws twitter4j.TwitterException
     */
    protected void checkStatus(String sKey) throws InterruptedException, TwitterException {
        Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
        RateLimitStatus value = rateLimitStatus.get(sKey);
        int remaining = value.getRemaining();
        if (remaining < 1) {
            int seconds_until_reset = value.getSecondsUntilReset();
            System.out.println(String.format("must wait for %d seconds until limit reset", seconds_until_reset));
            Thread.sleep((seconds_until_reset + 1) * 1000l);
        }
    }
}
