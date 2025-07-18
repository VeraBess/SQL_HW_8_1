package ru.netology.sql.test;

import org.junit.jupiter.api.*;
import ru.netology.sql.data.DataHelper;
import ru.netology.sql.data.SQLHelper;
import ru.netology.sql.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.sql.data.SQLHelper.cleanAuthCodes;
import static ru.netology.sql.data.SQLHelper.cleanDatabase;

public class LoginTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoWithTestData();

    @AfterAll
    static void cleanDataBaseAll() {
        cleanDatabase();
    }

    @AfterEach
    void cleanTableAuthCodes() {
        cleanAuthCodes();
    }

    @BeforeEach
    void openLoginPage() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    @DisplayName("Валидные данные(логин, пароль, код)")
    void validLoginPasswordCode() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("невалидный пользователь(рандомный")
    void notValidUser() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.login(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("невалидный проверочный код")
    void notValidVerificationCode() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("Блокировка пользователя после трех неудачных попыток входа")
    void threeInvalidEntryUserAndBlockUser() {
        var validAuthInfo = DataHelper.getAuthInfoWithTestData(); //валидный пользователь
        var validLogin = validAuthInfo.getLogin(); // получаем валидный логин

        // три неудачных попытки ввода
        for (int i = 0; i < 3; i++) {
            var randomPassword = DataHelper.generateRandomPassword(); // генерируем случайный пароль
            var invalidUserPass = new DataHelper.AuthInfo(validLogin, randomPassword); // создаем юзера с валидным логином и случайным паролем

            loginPage.login(invalidUserPass);  // Вход с этими данными
            loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
            loginPage.clearLoginFields(); //чистка полей
        }

        // четвертая попытка с корректными данными (ожидаемая блокировка)
        loginPage.login(validAuthInfo);
        loginPage.verifyErrorNotification("Пользователь заблокирован");
    }
}
