/*
 *
 *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.antonioleiva.mvpexample.app.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.antonioleiva.mvpexample.app.R;
import com.antonioleiva.mvpexample.app.main.MainActivity;
//在mvp中，Activity就是View，所有对逻辑的操作都交由Interactor来做；而所有的对视图的更新以及对逻辑操作的发起交由Presenter来做
//presenter一头持有view，另一头单向持有interactor
//activity-view互相持有presenter；
//这里会有内存泄漏问题，比如presenter在进行耗时操作时退出了activity，则view会泄漏，在onDestroy里把presenter对view的引用清空掉
//有一个问题是此时Activity（View）需要提供一堆的api对外，让presenter根据数据来操作，会有点麻烦，但设计会更独立
public class LoginActivity extends Activity implements LoginView, View.OnClickListener {

    private ProgressBar progressBar;
    private EditText username;
    private EditText password;
    private LoginPresenter presenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        findViewById(R.id.button).setOnClickListener(this);

        presenter = new LoginPresenterImpl(this);
    }

    @Override protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override public void setUsernameError() {
        username.setError(getString(R.string.username_error));
    }

    @Override public void setPasswordError() {
        password.setError(getString(R.string.password_error));
    }

    @Override public void navigateToHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();//finish之后，就不会再退回到这个activity里了
    }

    @Override public void onClick(View v) {
        //对view的更新操作，对逻辑的操作发起，都在presenter里进行，这里不直接取数据判断然后修改view，而交给presenter去处理
        presenter.validateCredentials(username.getText().toString(), password.getText().toString());
    }
}
