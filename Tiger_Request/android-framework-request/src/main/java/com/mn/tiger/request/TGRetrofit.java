package com.mn.tiger.request;

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

        @Override
        public void onResponse(Call<T> call, Response<T> response)
        {
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
                    onRequestError(response.code(), response.message());
                }
                else
                {
                    this.success = false;
                    onRequestError( -1, "");
                }
            }

            /**
             * 请求结束
             */
            onReqeustOver(call);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t)
        {
            this.success = false;
            onRequestError(-1, t.getMessage());
            onReqeustOver(call);
        }

        public boolean hasError(Call<T> call, Response<T> response)
        {
            return false;
        }

        public abstract void onRequestSuccess(Response<T> response, D data);

        public abstract D parseOriginalResponse(Response<T> response);

        public void onRequestError( int code ,String message)
        {

        }

        public void onReqeustOver(Call<T> call)
        {

        }

        public boolean isSuccess()
        {
            return success;
        }
    }
}
