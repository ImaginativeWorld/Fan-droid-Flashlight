/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.imaginativeworld.fan;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class aboutActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnOk;
    TextView dev_url, txtVersion, src_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(aboutActivity.this);

        dev_url = (TextView) findViewById(R.id.dev_url);
        dev_url.setOnClickListener(aboutActivity.this);

        src_url = (TextView) findViewById(R.id.src_url);
        src_url.setOnClickListener(aboutActivity.this);

        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtVersion.setText(String.format(getString(R.string.version_txt), BuildConfig.VERSION_NAME));


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnOk:

                finish();

                break;

            case R.id.dev_url:

                //Go to the url
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.website)));
                startActivity(browserIntent);

                break;

            case R.id.src_url:

                //Go to the url
                Intent browserIntent_src = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.website_github)));
                startActivity(browserIntent_src);

                break;
        }
    }
}
