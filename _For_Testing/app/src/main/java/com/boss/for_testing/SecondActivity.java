package com.boss.for_testing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setPadding(16, 16, 16, 16);

        Bundle arguments = getIntent().getExtras();
        final Product product;

        product = (Product) arguments.getSerializable(Product.class.getSimpleName());
        if (arguments != null) {
            textView.setText("Name: " + product.getName() + "\nCompany: " + product.getCompany() +
                    "\nPrice: " + String.valueOf(product.getPrice()));
        }

        TextView log = new TextView(this);
        log.setText(product.getText());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(textView);
        layout.addView(log);

        setContentView(layout);
    }
}
