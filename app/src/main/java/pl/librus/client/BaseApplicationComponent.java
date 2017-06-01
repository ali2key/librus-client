package pl.librus.client;

import pl.librus.client.ui.LoginActivity;

/**
 * Created by robwys on 27/03/2017.
 */

public interface BaseApplicationComponent {

    UserComponent plus(UserModule userModule);

    void inject(LoginActivity loginActivity);

    void inject(MainApplication mainApplication);
}
