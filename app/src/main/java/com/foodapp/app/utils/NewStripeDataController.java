package com.foodapp.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class NewStripeDataController {
    CallBackSuccess callBackSuccess;
    String strKey;

    public NewStripeDataController(Activity context, String strKey) {
        callBackSuccess = (CallBackSuccess) context;
        this.strKey = strKey;
    }

    public void CreateToken(final Card card, Context context) {
        callBackSuccess.onstart();
        Stripe stripe = new Stripe(context, strKey);
        stripe.createCardToken(
                card,
                new ApiResultCallback<Token>() {
                    public void onSuccess(Token token) {
                        Log.e("Stripe Token", token.getId());
                        callBackSuccess.success(token);
                    }

                    public void onError(Exception error) {
                        callBackSuccess.failer(error);
                    }
                }
        );
    }
}
