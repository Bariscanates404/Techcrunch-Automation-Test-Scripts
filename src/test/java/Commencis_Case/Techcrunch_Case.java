package Commencis_Case;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;

public class Techcrunch_Case {
    WebDriver driver;
    static int workingLinks = 0;  // To verify working links (at line 79)

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get("https://techcrunch.com/");
    }
    @After
    public void Teardown() {
        driver.close();
    }

    @Test
    //  Test case “The Latest News” list, verify followings; a. each news has an author b. each news has an image
    //  Site dynamicly changing so that's why we used Css Selectors now we can handle web site dynamicly.
    public void Test01() {
        List<WebElement> imageElements = driver.findElements(By.cssSelector("article figure:last-child img"));
        List<WebElement> authorElements = driver.findElements(By.cssSelector("article header span:last-child a"));
        List<WebElement> lastestNewElements = driver.findElements(By.cssSelector("article header h2 a"));

        for (WebElement each : imageElements        // List of all images on latest news list
        ) {
            Assert.assertTrue(each.isDisplayed());
            Assert.assertTrue(each.isEnabled());
        }
        for (WebElement each : authorElements       // List of all authors on latest news list
        ) {                                         // We only count 1 author for a news article because we plan to verify by comparing them.
            Assert.assertTrue(each.isDisplayed());
            Assert.assertTrue(each.isEnabled());
        }
        for (WebElement each : lastestNewElements   // List of all images on latest news list
        ) {
            Assert.assertTrue(each.isDisplayed());
            Assert.assertTrue(each.isEnabled());
        }
    //  Comparing numbers of the article, image and authors to verify every article has one.
        Assert.assertEquals(imageElements.size(), authorElements.size(), lastestNewElements.size());
    }

    @Test
    //  To compare browser title and content title
    public void Test02() throws InterruptedException {
        driver.findElement(By.cssSelector("article header h2 a")).click();
        String fullContentNewsTitle = driver.findElement(By.cssSelector("article header h1")).getText();
        String browserTitle = driver.getTitle();
        browserTitle=browserTitle.substring(0,browserTitle.length()-13);    //  We deleted " | Techcrunch" text part on the browser title to get the raw title to check equality
        Assert.assertEquals(browserTitle,fullContentNewsTitle);             //  We use equals() to get highly sensitive test results.

        List<WebElement> linkList = driver.findElements(By.cssSelector(".article-content>p a"));
        for (WebElement links : linkList
        ) {
            String url = links.getAttribute("href");
            verifyLinkActive(url);
            Thread.sleep(1000);
        }
        System.out.println("Total link count: " + linkList.size() + "  ||   Working link count: " + workingLinks);
        Assert.assertTrue(linkList.size() == workingLinks);
    }

    public static void verifyLinkActive(String linkUrl) {
    //  We are using this method to read URL responses to check errors
    //  Page responses 2XX: generally "OK" - 3XX: relocation/redirect "OK"
    //  -4XX: client error(some of the pages that sends 4XX responses can be work but need attention) - 5XX: server error
        try {
            URL url = new URL(linkUrl);
            HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
            httpURLConnect.setConnectTimeout(5000);
            httpURLConnect.connect();
            if (httpURLConnect.getResponseCode() >= 1 && httpURLConnect.getResponseCode() <= 399) {
                workingLinks++;
                System.out.println(linkUrl + " - " + httpURLConnect.getResponseMessage());
            }
            if (httpURLConnect.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println(linkUrl + " - " + httpURLConnect.getResponseMessage() + " - " + HttpURLConnection.HTTP_NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("Exception thrown!!");
        }
    }
}
