package com.boss.for_testing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {

    TextView tv_name;
    TextView tv_company;
    TextView tv_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        tv_name = (TextView) findViewById(R.id.name);
        tv_company = (TextView) findViewById(R.id.company);
        tv_price = (TextView) findViewById(R.id.price);

        Bundle arguments = getIntent().getExtras();
        final Product product;

        product = (Product) arguments.getSerializable(Product.class.getSimpleName());
        if (arguments != null) {
            tv_name.setText(product.getName());
            tv_company.setText(product.getCompany());
            tv_price.setText(String.valueOf(product.getPrice()));
        }
    }
}
