/*
 * Copyright (C) 2012 Brandon Tate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.truthower.suhang.fragmentedtime.business.web;

/**
 * Text Selection Listener Interface
 *
 * @author Brandon Tate
 */
public interface JavaScriptCallBackListener {
    //去某个页面
    void toPage(String activityName);

    //去某个页面
    void toPage(String activityName, String params);

    //需要登录
    void goToLogin(String string);

    void closeHtml();

    //关闭上下拉刷新空间
    void closePullRefresh();
}
