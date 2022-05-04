import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



import static sun.misc.Version.println;

public class WeiboTester {
    WebDriver driver;
    boolean isGetCookie = false;
    String cookiePath = "weibocookie.txt";
    String beginDay = "2022-04-12";
    String endDay = "2022-04-20";
    Set<String> keywords = new HashSet<String>() {{
        add("女拳");
        add("女权");
        add("性别歧视");
    }};
    // String cookiePath;
    boolean getCookie = false;
    public WeiboTester(String site){
        System.setProperty("webdriver.chrome.driver","D:\\Files/drive/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(site);
        //driver.get(site);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // cookiePath="Cookies.txt";
    }

    /*Get Cookies*/
    public void GetCookies() throws InterruptedException, IOException {
        Set<Cookie> cookies;
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(cookiePath));
        long sleepTime = 120000;
        Thread.sleep(sleepTime);
        cookies = driver.manage().getCookies();
        objectOutputStream.writeObject(cookies);
    }


    /*Set Cookies*/
    public void SetCookies() throws IOException, ClassNotFoundException, InterruptedException {
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory",".");
        options.setExperimentalOption("prefs",prefs);
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(cookiePath));
        HashSet<Cookie> cookies = (HashSet<Cookie>) objectInputStream.readObject();
        for (Cookie cookie : cookies){
            driver.manage().addCookie(cookie);
        }
        driver.navigate().refresh();
        Thread.sleep(3000);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }

    public void PreSearch() throws IOException, ClassNotFoundException, InterruptedException {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement searchButton = driver.findElement(By.className("woo-input-main"));
        String mainHandle, searchHandle = null, keywordInput = "";
        Set<String> webHandles;
        mainHandle = driver.getWindowHandle();
        keywordInput = "Test";
        searchButton.sendKeys(keywordInput);
        searchButton.sendKeys(Keys.ENTER);
        webHandles = driver.getWindowHandles();
        for (String handle: webHandles){
            if (!mainHandle.equals(handle)){
                searchHandle = handle;
                break;
            }
        }
        driver.switchTo().window(mainHandle);
        driver.close();
        driver.switchTo().window(searchHandle);
        SetCookies();
    }


    public void GetInfo(Calendar dd) throws InterruptedException, IOException {
        for(int i = 0; i <= 23; ++i){
            GetHourInfo(dd, i);
        }
    }

    public void GetHourInfo(Calendar dd, int hour) throws InterruptedException, IOException {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement searchButton = driver.findElement(By.className("woo-input-main"));
        WebElement superSearch, superSearchLayer, superSearchText;
        WebElement stimeButton, etimeButton, sHourButton, eHourButton, mainSelect, monthSelect, yearSelect, selectButton, daySelect;
        Select monthSelector, yearSelector, sHourSelector, eHourSelector;
        String mainHandle, searchHandle = null, keywordInput = "", fileName;
        Set<String> webHandles;
        mainHandle = driver.getWindowHandle();

        fileName = dd.getTime().toString() + "_" + hour;
        fileName = fileName.replace(" ","_").replace(":","")+".json";

        for (String keyword:keywords){
            keywordInput = keywordInput + keyword;
            keywordInput = keywordInput + "|";
        }
        keywordInput = keywordInput.substring(0,keywordInput.length()-1);

        searchButton.sendKeys(Keys.CONTROL,"a");
        searchButton.sendKeys(Keys.BACK_SPACE);
        searchButton.sendKeys(keywordInput);
        searchButton.sendKeys(Keys.ENTER);

        superSearch = driver.findElement(By.linkText("高级搜索"));
        superSearch.click();

        driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
        superSearchLayer = driver.findElement(By.className("m-layer"));

        //Set the start time
        stimeButton = driver.findElement(By.name("stime"));
        stimeButton.click();

        mainSelect = driver.findElement(By.className("m-caldr"));
        monthSelect = mainSelect.findElement(By.className("month"));
        yearSelect = mainSelect.findElement(By.className("year"));


        monthSelector = new Select(monthSelect);
        yearSelector = new Select(yearSelect);

        if (!Objects.equals(monthSelect.getAttribute("value"), dd.get(Calendar.MONTH) + "")) {
            monthSelector.selectByValue(dd.get(Calendar.MONTH) + "");
        }
        //if (!Objects.equals(yearSelect.getAttribute("value"), dd.get(Calendar.YEAR) + "")){
        //    yearSelector.selectByValue(dd.get(Calendar.YEAR)+"");
        //}
        daySelect = mainSelect.findElement(By.linkText(dd.get(Calendar.DATE)+""));
        daySelect.click();

        sHourButton = driver.findElement(By.name("startHour"));
        sHourSelector = new Select(sHourButton);
        if(!Objects.equals(sHourButton.getAttribute("value"), hour + "")){
            sHourSelector.selectByValue(hour+"");
        }

        //Set the end time
        if (hour==23){
            dd.add(Calendar.DAY_OF_MONTH, 1);
        }

        etimeButton = driver.findElement(By.name("etime"));
        etimeButton.click();

        mainSelect = driver.findElement(By.className("m-caldr"));
        monthSelect = mainSelect.findElement(By.className("month"));
        yearSelect = mainSelect.findElement(By.className("year"));

        monthSelector = new Select(monthSelect);
        yearSelector = new Select(yearSelect);

        if (!Objects.equals(monthSelect.getAttribute("value"), dd.get(Calendar.MONTH) + "")) {
            monthSelector.selectByValue(dd.get(Calendar.MONTH) + "");
        }
        //if (!Objects.equals(yearSelect.getAttribute("value"), dd.get(Calendar.YEAR) + "")){
        //    yearSelector.selectByValue(dd.get(Calendar.YEAR)+"");
        //}

        daySelect = mainSelect.findElement(By.linkText(dd.get(Calendar.DATE)+""));
        daySelect.click();

        eHourButton = driver.findElement(By.name("endHour"));
        eHourSelector = new Select(eHourButton);
        if(!Objects.equals(eHourButton.getAttribute("value"), (hour+1)%24+"")){
            eHourSelector.selectByValue((hour+1)%24+"");
        }
        //Check and go to crawl the data!
        selectButton = superSearchLayer.findElement(By.className("btn-box"));
        List<WebElement> selectList = selectButton.findElements(By.xpath("./*"));
        selectList.get(1).click();

        try {
            GetDetails(fileName);
        }
        catch(Exception e)
        {
            System.out.println("hhh No woobo found!");
        }
    }

    public void GetDetails(String fileName) throws IOException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        String textContent, mainHandle, userHandle=null;
        String genderClass, followerText;
        WebElement content, txtContent, actContent, userButton;
        WebElement userName, userArea, userGender, userFollower, tweetTime, userInfo;
        WebElement toolWE, toolWE1, toolWE2;
        WebElement pageSelect = null;
        boolean multiPage = true;
        int pageNum = 1;
        List<WebElement> details, fcf, pageList;
        Set<String> webHandles;
        String num, pageUrl;

        try {
            pageSelect = driver.findElement(By.xpath("//*[@id=\"pl_feedlist_index\"]/div[3]/div/span/ul"));
        }
        catch (Exception e)
        {
            multiPage = false;
        }

        if (multiPage)
        {
            pageList = pageSelect.findElements(By.xpath("./*"));
            pageNum = pageList.size();
        }

        pageUrl = driver.getCurrentUrl();
        for(int page= 1;page<=pageNum; ++ page){
            if (multiPage){
                //reset the url
                driver.get(pageUrl+"&page="+page);
            }
            content = driver.findElement(By.className("main-full"));
            details = content.findElements(By.className("card"));
            mainHandle = driver.getWindowHandle();
            for(WebElement card: details) {
                jsonObject = new JSONObject();
                txtContent = card.findElement(By.className("txt"));
                actContent = card.findElement(By.className("card-act"));
                // the text content of this tweet
                textContent = txtContent.getText();
                jsonObject.put("Content", textContent);

                //the time of this tweet
                tweetTime = card.findElement(By.className("from"));
                jsonObject.put("Time", tweetTime.getText().substring(0, 12));

                //the forward, comment, favor of this tweet
                fcf = actContent.findElement(By.xpath("./*")).findElements(By.xpath("./*"));
                for (int i = 0; i < 3; ++i) {
                    WebElement f = fcf.get(i);
                    String tmp = f.getText();
                    switch (i) {
                        case 0:
                            if (tmp.contains("转发")) {
                                // seems like we cannot get the forward number from the website;
                                num = "0";
                            } else {
                                num = tmp;
                            }
                            jsonObject.put("forward", num);
                            break;
                        case 1:
                            if (tmp.contains("评论")) {
                                num = "0";
                            } else {
                                num = tmp;
                            }
                            jsonObject.put("comment", num);
                            break;
                        case 2:
                            if (tmp.contains("赞")) {
                                num = "0";
                            } else {
                                num = tmp;
                            }
                            jsonObject.put("favor", num);
                            break;
                    }
                }

                //the user information
                userButton = card.findElement(By.className("avator"));
                userButton.click();
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                mainHandle = driver.getWindowHandle();
                webHandles = driver.getWindowHandles();
                for (String handle : webHandles) {
                    if (!mainHandle.equals(handle)) {
                        userHandle = handle;
                        break;
                    }
                }
                driver.switchTo().window(userHandle);
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                userInfo = driver.findElement(By.className("Main_full_1dfQX"));

                // the user ID
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                userName = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/main/div/div/div[2]/div[1]/div[1]/div[2]/div[2]/div[1]"));
                jsonObject.put("ID", userName.getText());

                // the user gender
                driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                userGender = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/main/div/div/div[2]/div[1]/div[1]/div[2]/div[2]/div[1]/span[1]"));
                genderClass = userGender.findElements(By.xpath("./*")).get(0).getAttribute("class");
                if (genderClass.contains("female")) {
                    jsonObject.put("Gender", "female");
                } else {
                    if (genderClass.contains("male")) {
                        jsonObject.put("Gender", "male");
                    } else {
                        jsonObject.put("Gender", "unknown");
                    }
                }

                // the user follower
                userFollower = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/main/div/div/div[2]/div[1]/div[1]/div[2]/div[2]/div[2]/a[1]"));
                followerText = userFollower.getText();
                jsonObject.put("Follower", followerText.replace("粉丝", ""));

                // the user area
                try{
                    userArea = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div[2]/main/div/div/div[2]/div[1]/div[1]/div[3]/div/div/div[1]/div[3]/div/div/div[2]/div"));
                    //*[@id="app"]/div[1]/div[2]/div[2]/main/div/div/div[2]/div[1]/div[1]/div[3]/div/div/div[1]/div[2]/div/div/div[2]/div
                    jsonObject.put("Area", userArea.getText().substring(5));
                    //jsonArray.put(jsonObject);
                }
                catch (Exception e){
                    try {
                        userArea = driver.findElement(By.xpath("//a[contains(text(), ’IP属地’)]"));
                        jsonObject.put("Area", userArea.getText().substring(5));
                    }
                    catch (Exception e1){
                        jsonObject.put("Area", "unknown");
                    }
                    //jsonObject.put("Area", "unknown");
                }
                jsonArray.put(jsonObject);
                driver.close();
                driver.switchTo().window(mainHandle);
            }
        }


        //write into the file

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");
        osw.write(jsonArray.toString());
        osw.flush();
        osw.close();
    }


    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        WeiboTester weiboTester = new WeiboTester("https://www.weibo.com");
        if (weiboTester.isGetCookie){
            weiboTester.GetCookies();
        }
        weiboTester.PreSearch();
        Date d1 = sdf.parse(weiboTester.beginDay);
        Date d2 = sdf.parse(weiboTester.endDay);
        Date tmp = d1;
        Calendar dd = Calendar.getInstance();
        dd.setTime(d1);
        while(tmp.getTime()<=d2.getTime()){
            weiboTester.GetInfo(dd);
        }
    }
}
