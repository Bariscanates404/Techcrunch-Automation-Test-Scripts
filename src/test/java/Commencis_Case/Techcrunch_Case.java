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
    static int workingLinks = 0;

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

        driver.quit();
    }

    @Test
    //  For “The Latest News” list, verify followings; a. each news has an author b. each news has an image
    public void test01() {
        List<WebElement> imageElements = driver.findElements(By.cssSelector("article figure:last-child img"));      //Site dynamicly changing so
        List<WebElement> authorElements = driver.findElements(By.cssSelector("article header span:last-child a"));  //that's why I used Css Selectors
        List<WebElement> lastestNewElements = driver.findElements(By.cssSelector("article header h2 a"));           //now I can handle web site dynamicly.

        for (WebElement each : imageElements        // List of all images on latest new list
        ) {
            Assert.assertTrue(each.isDisplayed());
            Assert.assertTrue(each.isEnabled());
        }
        for (WebElement each : authorElements       // List of all authors on latest new list
        ) {                                         //I only count 1 author for a news article because I plan to verify by comparing them.
            Assert.assertTrue(each.isDisplayed());
            Assert.assertTrue(each.isEnabled());
        }
        for (WebElement each : lastestNewElements   // List of all images on latest new list
        ) {
            Assert.assertTrue(each.isDisplayed());
            Assert.assertTrue(each.isEnabled());
        }

        Assert.assertEquals(imageElements.size(), authorElements.size(), lastestNewElements.size()); //Comparing numbers of the article,
    }                                                                                                //image and authors to verify every article has one.

    @Test               //To compare browser title and content title
    public void Test02() throws InterruptedException {
        driver.findElement(By.cssSelector("article header h2 a")).click();
        String browserTitle = driver.findElement(By.cssSelector("article header h2 a")).getText();
        String fullContentNewsTitle = driver.getTitle();
        Assert.assertTrue(fullContentNewsTitle.contains(browserTitle));

        List<WebElement> linkList = driver.findElements(By.cssSelector(".article-content>p a"));    //Links list on content text
        for (WebElement links : linkList                                                            //Verifying links single by single
        ) {
            String url = links.getAttribute("href");
            System.out.println(url);
            verifyLinkActive(url);
            Thread.sleep(1000);
        }

        System.out.println("Total link count: " + linkList.size() + "  ||   Working link count: " + workingLinks);
        Assert.assertTrue(linkList.size() == workingLinks);                                   //Verifying working links
    }

    public static void verifyLinkActive(String linkUrl) {   //Just to read URL responses
        try {
            URL url = new URL(linkUrl);
            HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
            httpURLConnect.setConnectTimeout(5000);
            httpURLConnect.connect();
            if (httpURLConnect.getResponseCode() >= 1 && httpURLConnect.getResponseCode() <= 399) {     //To check working links
                workingLinks++;
                System.out.println(linkUrl + " - " + httpURLConnect.getResponseMessage());
            }
            if (httpURLConnect.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {                  //To check broken link
                System.out.println(linkUrl + " - " + httpURLConnect.getResponseMessage() + " - " + HttpURLConnection.HTTP_NOT_FOUND);
            }
        } catch (Exception e) {
        }
    }
}
