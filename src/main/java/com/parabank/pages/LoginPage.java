package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By loginButton   = By.xpath("//input[@value='Log In']");
    private final By errorMessage  = By.cssSelector("p.error");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js     = (JavascriptExecutor) driver;
    }

    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        // JS direct assignment — no keyboard events that could trigger autofill
        js.executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input',  {bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            field, username
        );
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        // JS direct assignment — no keyboard events that could trigger autofill
        js.executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input',  {bubbles:true}));" +
            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            field, password
        );
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
