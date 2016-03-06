package com.mn.tiger.request;

import android.app.Activity;
import android.content.Context;

import com.mn.tiger.app.TGActionBarActivity;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Dalang on 2016/3/6.
 */
public class TGRetrofit
{
    protected static String stringify(RequestBody requestBody)
    {
        StringBuilder builder = new StringBuilder();
        if(requestBody instanceof FormBody)
        {
            FormBody formBody = (FormBody) requestBody;
            int size = formBody.size();
            for (int i = 0; i < size; i++)
            {
                builder.append(formBody.encodedName(i));
                builder.append(" = ");
                builder.append(formBody.encodedValue(i));
                builder.append(" ; ");
            }
        }
        return builder.toString();
    }

    public abstract static class Callback<T, D> implements retrofit2.Callback<T>
    {
        private boolean success = false;

        private Context context;

        public Callback(Context context)
        {
            this.context = context;
        }

        public Context getContext()
        {
            return context;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response)
        {
            if(null != context && context instanceof Activity && ((Activity)context).isFinishing())
            {
                if(context instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity)context).cancel(call);
                }
                return;
            }

            if(call.isCanceled())
            {
                if(null != context && context instanceof TGActionBarActivity)
                {
                    ((TGActionBarActivity)context).dequeue(call);
                }
                return;
            }

            if(!hasError(call, response))
            {
                this.success = true;
                onRequestSuccess(response, parseOriginalResponse(response));
            }
            else
            {
                if(null != response)
                {
                    this.success = false;
                    onRequestError(response.code(), response.message(), response);
                }
                else
                {
                    this.success = false;
                    onRequestError( -1, "", response);
                }
            }

            /**
             * 请求结束
             */
            onRequestOver(call);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t)
        {
            this.success = false;
            onRequestError(-1, t.getMessage(), null);
            onRequestOver(call);
        }

        public boolean hasError(Call<T> call, Response<T> response)
        {
            return false;
        }

        public abstract void onRequestSuccess(Response<T> response, D data);

        public abstract D parseOriginalResponse(Response<T> response);

        public void onRequestError( int code ,String message, Response<T> response)
        {

        }

        public void onRequestOver(Call<T> call)
        {
            if(null != context && context instanceof TGActionBarActivity)
            {
                ((TGActionBarActivity)context).dequeue(call);
            }
        }

        public boolean isSuccess()
        {
            return success;
        }
    }
}
