package com.mn.tiger.app;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mn.tiger.R;
import com.mn.tiger.utility.ToastUtils;
import com.norbsoft.typefacehelper.TypefaceHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TGWebViewActivity extends TGActionBarActivity
{
    /**
     * WebViewActivity的标题
     */
    public static final String WEBVIEW_ACTIVITY_TITLE = "bar_title";

    /**
     * WebViewActivity显示的页面的URL地址
     */
    public static final String URL = "url";

    private WebView mWebView;

    private View mProgress;

    private String barTitle;

    private String url;

    /**
     * 额外添加的header
     */
    private Map<String, String> addtionalHeader = new HashMap<String, String>();

    /**
     * 消息头里面的Referer字段
     */
    private String refererUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        mWebView = (WebView)findViewById(R.id.webview);
        mProgress = (View)findViewById(R.id.progress);

        TypefaceHelper.typeface(this);

        barTitle = getIntent().getStringExtra(WEBVIEW_ACTIVITY_TITLE);
        url = getIntent().getStringExtra(URL);
        setupViews();
    }

    private void setupViews()
    {
        setBarTitleText(barTitle);
        showLeftBarButton(true);

        mWebView.getSettings().setJavaScriptEnabled(true);
        // 创建WebViewClient对象
        WebViewClient wvc = new WebViewClient()
        {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器加载页面
                mWebView.loadUrl(url, getAddtionalHeader());
                refererUrl = url;
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
                super.onLoadResource(view, url);
            }

        };
        mWebView.setWebViewClient(wvc);
        mWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;

                int progressUnit = width / 100;

                LayoutParams params = mProgress.getLayoutParams();
                params.width = progressUnit * newProgress;
                mProgress.setLayoutParams(params);

                if (newProgress == 100)
                {
                    mProgress.setVisibility(View.GONE);
                }
                else
                {
                    mProgress.setVisibility(View.VISIBLE);
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        refererUrl = url;
        mWebView.loadUrl(url);
    }

    /**
     * 后去additionalHeader
     *
     * @return
     */
    private Map<String, String> getAddtionalHeader()
    {
        addtionalHeader.put("Referer", refererUrl);
        return addtionalHeader;
    }

    public void showToast(int textResId)
    {
        ToastUtils.showToast(this, textResId);
    }

    public void showToast(String text)
    {
        ToastUtils.showToast(this, text);
    }

}
