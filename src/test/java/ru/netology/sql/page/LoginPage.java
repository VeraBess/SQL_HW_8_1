package ru.netology.sql.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.sql.data.DataHelper;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login] input");
    private final SelenideElement passwordField = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldHave(exactText(expectedText)).shouldBe(visible);
    }
    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        login(info);
        return new VerificationPage();
    }
    public void login(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }

    public void clearLoginFields() {
        loginField.shouldBe(visible).press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        passwordField.shouldBe(visible).press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }
}
