package com.parabank.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

public class BaseTest {

    protected WebDriver driver;

    private static final String BASE_URL = "https://parabank.parasoft.com/parabank/index.htm";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().browserVersion("151").setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--incognito");
        options.addArguments("--disable-autofill-keyboard-accessory-view");
        options.addArguments("--disable-features=AutofillEnableAccountStorageForIneligibleCountries");
        options.setExperimentalOption("prefs", Map.of(
            "credentials_enable_service", false,
            "profile.password_manager_enabled", false,
            "autofill.profile_enabled", false,
            "autofill.enabled", false
        ));

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get(BASE_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
