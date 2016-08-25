package io.vithor.htmltextview.sample;

import android.app.Activity;
import android.os.Bundle;

import com.vithor.textviewforhtml.R;

import io.vithor.htmltextview.HtmlTextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HtmlTextView htmlTextView = (HtmlTextView) findViewById(R.id.htvHtml);
//        String text="<p style=\"text-align:center\"><em><strong>YSL TEST</strong></em></p>\n" +
//                "\n" +
//                "<p style=\"text-align:center\">some text <span style=\"color:#FF0000\">with color</span> asdflkamsdf</p>\n" +
//                "\n" +
//                "<p style=\"text-align:center\"> lkasmdflsakdfm</p>\n" +
//                "\n" +
//                "<p style=\"text-align:center\">kdmafslk m <span style=\"font-size:20px\">lk amsdlkf m</span></p>\n" +
//                "\n" +
//                "<p style=\"text-align:center\">laksmdfa <span style=\"color:#EE82EE\">lkasdmf lkas</span> kjansfkj n</p>\n" +
//                "\n" +
//                "<p style=\"text-align:right\">align right style</p>\n" +
//                "\n" +
//                "<p style=\"text-align:center\">center style</p>\n" +
//                "\n" +
//                "<p align=\"right\">align right tag attr</p>\n" +
//                "\n" +
//                "<p style=\"text-align:right;color:#FF0000\">align right style + color</p>\n" +
//                "\n" +
//                "<p align=\"right\" style=\"color:#FF0000\">align right attr + color style</p>\n" +
//                "\n" +
//                "<ol>\n" +
//                "\t<li>item</li>\n" +
//                "\t<li>item 2</li>\n" +
//                "\t<li>item 3</li>\n" +
//                "</ol>\n" +
//                "\n" +
//                "<ul>\n" +
//                "\t<li>bullet 4</li>\n" +
//                "\t<li>bullet 5</li>\n" +
//                "</ul>\n" +
//                "\n" +
//                "<blockquote>\n" +
//                "<p align='right'>quote &nbsp;test</p>\n" +
//                "</blockquote>\n" +
//                "\n" +
//                "<p><a href=\"http://vithor.io\">clickable link</a></p>\n";

        // TODO: FIX <b>Bold</b> without <p> </p> crash
        String text =
                "<b>Bold</b>\n" +
//                                "\n" +
                "<i>Italic</i>\n" +
//                                "\n" +
//                "<bi color='#CCFF00'>Bold & Italic</bi>\n" //+
//                "<p align='justify'>alignment</p>\n" +
//                "<p align='center'>alignment center</p>\n" //+
//                "<p align='right'>alignment right</p>\n" //+
                "<p style=\"text-align:right;color:#FF0000\">align right style + color</p>\n" //+
//                "<p style=\"text-align:right\">align right using style</p>\n"
                ;
        htmlTextView.setHtmlText(text);
    }
}
