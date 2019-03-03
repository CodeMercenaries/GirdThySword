package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;

import java.util.Arrays;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class HelpAnswerActivity extends AppCompatActivity {

    List<String> FAQS;
    List<String> ANSWERS_TO_FAQS;

    FontHelper fontHelper;

    TextView question;
    TextView answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fontHelper = new FontHelper();
        fontHelper.initialize(this);
        setContentView(R.layout.activity_help_answer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FAQS = Arrays.asList(
                getString(R.string.what_can_this_app_do),
                getString(R.string.im_confused_where_do_i_get_an_overview),
                getString(R.string.how_do_i_add_new_sections_to_memorize),
                getString(R.string.how_do_i_know_when_to_review_a_particular_chunk),
                getString(R.string.how_do_i_report_a_bug_or_request_a_feature));

        ANSWERS_TO_FAQS = Arrays.asList(
                getString(R.string.what_can_this_app_do_answer),
                getString(R.string.im_confused_where_do_i_get_an_overview_answer),
                getString(R.string.how_do_i_add_new_sections_to_memorize_answer),
                getString(R.string.how_do_i_know_when_to_review_a_particular_chunk_answer),
                getString(R.string.how_do_i_report_a_bug_or_request_a_feature_answer));

        int faqPos = getIntent().getIntExtra("FAQ_POS", 0);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);

        question.setText(FAQS.get(faqPos));
        answer.setText(ANSWERS_TO_FAQS.get(faqPos));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
