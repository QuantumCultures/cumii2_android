package sg.lifecare.cumii.data;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class HttpInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401) {

        }
        return response;
    }
}
