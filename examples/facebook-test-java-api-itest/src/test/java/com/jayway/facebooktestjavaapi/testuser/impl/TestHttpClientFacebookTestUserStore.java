package com.jayway.facebooktestjavaapi.testuser.impl;

import com.jayway.facebooktestjavaapi.testuser.FacebookTestUserAccount;
import com.jayway.facebooktestjavaapi.testuser.FacebookTestUserStore;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: tobias
 * Date: 1/13/11
 * Time: 7:29 AM
 */
public class TestHttpClientFacebookTestUserStore {

    private static FacebookTestUserStore facebookStore;

    private JSONParser parser = new JSONParser();
    private static FacebookTestUserAccount account;
    private List<FacebookTestUserAccount> createdAccounts = new LinkedList<FacebookTestUserAccount>();

    @BeforeClass
    public static void beforeAllTests() throws IOException {
        Properties properties = new Properties();
        InputStream stream = TestHttpClientFacebookTestUserStore.class.getClassLoader().getResourceAsStream("facebook-app.properties");
        if (stream == null) {
            fail("Could not load 'facebook-app.properties");
        }
        properties.load(stream);
        facebookStore = new HttpClientFacebookTestUserStore(properties.getProperty("facebook.appId"), properties.getProperty("facebook.appSecret"));
        facebookStore.deleteAllTestUsers();
        account = facebookStore.createTestUser(true, "");
    }

    @AfterClass
    public static void afterAllTests() {
        if (facebookStore != null) {
            facebookStore.deleteAllTestUsers();
        }
    }

    @After
    public void afterEachTest() {
        deleteCreatedAccounts();
    }

    @Test
    public void testCreateFacebookAccount() {

        FacebookTestUserAccount createdAccount = createAccount();

        assertNotNull(createdAccount);
        assertNotNull(createdAccount.id());
        assertNotNull(createdAccount.accessToken());
        assertNotNull(createdAccount.loginUrl());

        assertEquals(2, facebookStore.getAllTestUsers().size());
    }

    @Test
    public void testDeleteFacebookAccount() {
        FacebookTestUserAccount createdAccount = facebookStore.createTestUser(true, "");

        assertEquals(2, facebookStore.getAllTestUsers().size());

        createdAccount.delete();

        assertEquals(1, facebookStore.getAllTestUsers().size());
    }

    @Test
    public void testMakeFriends() {
        FacebookTestUserAccount account1 = createAccount();
        FacebookTestUserAccount account2 = createAccount();

        account1.makeFriends(account2);

        String friends1 = account1.getFriends();
        String friends2 = account2.getFriends();

        assertTrue("The friends list for account1 does not contain account2", friends1.contains(account2.id()));
        assertTrue("The friends list for account2 does not contain account1", friends2.contains(account1.id()));
    }

    @Test
    public void testGetUserDetails() {
        String userDetails = account.getUserDetails();

        assertTrue(userDetails.contains("name"));
        assertTrue(userDetails.contains("first_name"));
        assertTrue(userDetails.contains("last_name"));
        assertTrue(userDetails.contains("link"));
        assertTrue(userDetails.contains("gender"));
    }

    @Test
    public void testGetNewsFeed() throws Exception {
        assertContainsData(account.getNewsFeed());
    }

    @Test
    public void testGetProfileFeed() throws Exception {
        assertContainsData(account.getProfileFeed());
    }

    @Test
    public void testGetLikes() throws Exception {
        assertContainsData(account.getLikes());
    }

    @Test
    public void testGetMovies() throws Exception {
        assertContainsData(account.getMovies());
    }

    @Test
    public void testGetMusic() throws Exception {
        assertContainsData(account.getMusic());
    }

    @Test
    public void testGetBooks() throws Exception {
        assertContainsData(account.getBooks());
    }

    @Test
    public void testGetNotes() throws Exception {
        assertContainsData(account.getNotes());
    }

    @Test
    public void testGetPhotoTags() throws Exception {
        assertContainsData(account.getPhotoTags());
    }

    @Test
    public void testGetPhotoAlbums() throws Exception {
        assertContainsData(account.getPhotoAlbums());
    }

    @Test
    public void testGetVideoTags() throws Exception {
        assertContainsData(account.getVideoTags());
    }

    @Test
    public void testGetVideoUploads() throws Exception {
        assertContainsData(account.getVideoUploads());
    }

    @Test
    public void testGetEvents() throws Exception {
        assertContainsData(account.getEvents());
    }

    @Test
    public void testGetGroups() throws Exception {
        assertContainsData(account.getGroups());
    }

    @Test
    public void testGetCheckins() throws Exception {
        assertContainsData(account.getCheckins());
    }


    // Helpers
    private void assertContainsData(String json) throws ParseException {
        Object result = parser.parse(json);
        assertTrue("The result was not of type JSON", result instanceof JSONObject);
        JSONObject container = (JSONObject) result;
        assertNotNull("The result did not contain the 'data' field", container.get("data"));
        assertTrue("The value of the 'data' fields is not of type JSON Array", container.get("data") instanceof JSONArray);
    }

    private void deleteCreatedAccounts() {
        for (FacebookTestUserAccount createdAccount : createdAccounts) {
            createdAccount.delete();
        }
    }

    private FacebookTestUserAccount createAccount() {
        FacebookTestUserAccount account = facebookStore.createTestUser(true, "");
        createdAccounts.add(account);
        return account;
    }

}
