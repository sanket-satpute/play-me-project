package com.sanket_satpute_20.playme.project.account.bottom_fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.documentation.AppDocs;
import com.sanket_satpute_20.playme.project.account.data.documentation.AppDocsComments;
import com.sanket_satpute_20.playme.project.account.recycler.DocumentationParentRecycler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class PlayMeUseAccountDocumentationBottomFragment extends BottomSheetDialogFragment {

    RecyclerView documentation_recycler;
    RelativeLayout progress_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("hollow");
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog);
        View view = inflater.inflate(R.layout.fragment_play_me_use_account_documentation_bottom, container, false);
        initViews(view);

        progress_layout.setVisibility(View.VISIBLE);
        documentation_recycler.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture.supplyAsync(this::loadAllAppDocs).thenAccept(result -> {
                if (result != null) {
                    if (result.size() > 0) {
                        setRecycler(result);
                    } else {
                        unLoadDialog();
                    }
                } else {
                    unLoadDialog();
                }
            }).exceptionally(ex -> {
                unLoadDialog();
                return null;
            });
        } else {
            LoadInBackground loader = new LoadInBackground(new PlayMeUseAccountDocumentationBottomFragment());
            loader.execute();
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
    }

    private void initViews(View view) {
        documentation_recycler = view.findViewById(R.id.documentation_parent_recycler);
        progress_layout = view.findViewById(R.id.progress_bar);
    }

    private void setRecycler(ArrayList<AppDocs> docs_list) {
        progress_layout.setVisibility(View.GONE);
        documentation_recycler.setVisibility(View.VISIBLE);
//        after all assignments are completed set the adapter and attack the adapter to the recyclerview
        documentation_recycler.setAdapter(new DocumentationParentRecycler(requireActivity(), docs_list));
        documentation_recycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private ArrayList<AppDocs> loadAllAppDocs() {
        //        parent array list of all documents
        ArrayList<AppDocs> docsList = new ArrayList<>();

//        setting
        String title_1 = "Setting";

        ArrayList<Integer> app_pictures_1 = new ArrayList<>();
        app_pictures_1.add(R.drawable.firebase_user_logged_in_1);
        app_pictures_1.add(R.drawable.firebase_user_logged_out_2);


        String helper_txt_1 = "The settings consist of three pages. If you don't have an account, you'll see buttons to sign up " +
                "or sign in. If you're signed in, your basic details will be displayed. If an error occurs, a retry view will be" +
                " shown.";

        ArrayList<AppDocsComments> docs_comment_1_1 = new ArrayList<>();
        docs_comment_1_1.add(0, new AppDocsComments("User Name", "At the top, you'll see the current " +
                "user's full name displayed prominently in text."));
        docs_comment_1_1.add(1, new AppDocsComments("Coins", "At the bottom of the coin, you'll find " +
                "a text displaying the total number of coins you've earned, which can be redeemed for rewards."));
        docs_comment_1_1.add(2, new AppDocsComments("Money", "Next to your earned coins, you'll see " +
                "a text showing the total amount of withdrawable money you have at your disposal."));
        docs_comment_1_1.add(3, new AppDocsComments("Show Account", "At the bottom of your earned " +
                "coins and money, you'll find a button labeled 'Show Account'. Tapping on this button will take you to your " +
                "personal dashboard in the Account activity."));
        docs_comment_1_1.add(4, new AppDocsComments("Using Time", "At the bottom of the 'Show Account'" +
                " button, you'll see a progress bar that tracks the time you've spent using the app and how much time remains." +
                " Please note that the timer only runs when the app is open."));
        docs_comment_1_1.add(5, new AppDocsComments("User Picture", "In the top right corner, you'll" +
                " spot the user's profile picture. Tapping on it will open a dialog box displaying the user's information."));
        docs_comment_1_1.add(6, new AppDocsComments("Watch", "Watch an ad and get rewarded! Click the" +
                " button and watch the entire ad to earn your reward."));
        docs_comment_1_1.add(7, new AppDocsComments("Redeem", "Ready to cash in your coins? Simply " +
                "click 'redeem' to unlock access to our exclusive redeem coins activity! Convert your hard-earned coins into " +
                "cold, hard cash that you can withdraw anytime, anywhere. Get started now!"));
        docs_comment_1_1.add(8, new AppDocsComments("Documentation", "When you click on documentation," +
                " a helpful dialog appears at the bottom of your screen with detailed information about your account and how to" +
                " use it."));

        ArrayList<AppDocsComments> docs_comment_1_2 = new ArrayList<>();
        docs_comment_1_2.add(new AppDocsComments("Sign Up", "Join the PlayMe community today with just a few " +
                "clicks! Sign up now to create your account and unlock access to endless entertainment. Fill out a few basic " +
                "details, and voila! You're in! Don't miss out on the fun - join us now!"));
        docs_comment_1_2.add(new AppDocsComments("Sign In", "Access your PlayMe account with ease - " +
                "simply click 'Sign In' and you're in! Enjoy seamless and hassle-free login and unlock endless entertainment " +
                "options. Your account, your way - join us now and start exploring!"));
        docs_comment_1_2.add(new AppDocsComments("Watch", "See it to believe it! Click 'Watch' and get a " +
                "sneak peek of how your ad will look like. Our demo will show you exactly how your ad will be displayed, giving" +
                " you the confidence to make the most of your marketing campaign. Don't wait - click now and see for yourself!"));


        ArrayList<ArrayList<AppDocsComments>> app_info_1 = new ArrayList<>();
        app_info_1.add(0, docs_comment_1_1);
        app_info_1.add(1, docs_comment_1_2);

        AppDocs app_doc_1 = new AppDocs(title_1, app_pictures_1, helper_txt_1, app_info_1);

        docsList.add(app_doc_1);

//        account

        String title_2 = "Account / Dashboard";

        ArrayList<Integer> app_pictures_2 = new ArrayList<>();
        app_pictures_2.add(R.drawable.firebase_user_dashboard_1);
        app_pictures_2.add(R.drawable.firebase_user_dashboard_bars_and_time_2);
        app_pictures_2.add(R.drawable.firebase_user_dashboard_services_3);

        String helper_txt_2 = "On the account page, you'll find a dashboard displaying your information and daily activity. " +
                "You can view your money and customize your account settings from here.";

        ArrayList<AppDocsComments> docs_comment_2_1 = new ArrayList<>();
        docs_comment_2_1.add(new AppDocsComments("Greet Name", "Experience a personalized welcome like " +
                "never before! At the top left corner, your name is displayed for a warm and friendly greeting. Get ready to " +
                "feel right at home and enjoy a customized experience tailored just for you. Join us now and see your name in " +
                "lights!"));
        docs_comment_2_1.add(new AppDocsComments("Option's", "Power up your experience with one simple " +
                "click! Our menu option, located at the top right corner, unlocks a world of possibilities. Access a variety " +
                "of operations and features, manage your account, and explore new options - all at your fingertips. Don't miss" +
                " out - click now and take control of your experience!"));
        docs_comment_2_1.add(new AppDocsComments("User Picture", "In the top right corner, inside the " +
                "circle, you'll spot the user's profile picture. Tapping on it will open a dialog box displaying the user's " +
                "information."));
        docs_comment_2_1.add(new AppDocsComments("Money", "Below the text that shows the amount of money " +
                "in your account, you will find the total amount that is available for you to withdraw."));
        docs_comment_2_1.add(new AppDocsComments("Payout Method", "After checking your total account " +
                "balance, you will find the UPI ID, phone number, or email that you can use for receiving payouts."));
        docs_comment_2_1.add(new AppDocsComments("Currency Convert", "Right next to the payout options, " +
                "you'll find a button that allows you to change your currency. This change will be reflected in your total " +
                "account balance."));
        docs_comment_2_1.add(new AppDocsComments("Notification", "On the right side of your account " +
                "balance, you'll see a notification bell. If you have any payment-related notifications, a small dot will appear" +
                " above the bell to let you know."));
        ArrayList<AppDocsComments> docs_comment_2_2 = new ArrayList<>();
        docs_comment_2_2.add(new AppDocsComments("Using Time", "At the top of the page, you'll see a " +
                "progress bar that indicates how much time has passed and how much time is remaining. Just below the progress" +
                " bar and to the side, you'll find the text that shows the time completed and the time remaining."));
        docs_comment_2_2.add(new AppDocsComments("Chart Setting's", "After the time display, you'll see " +
                "a button labeled \"Modify\". By clicking on this button, you can easily modify the settings of the chart to " +
                "suit your preferences."));
        docs_comment_2_2.add(new AppDocsComments("show account Button", "At the bottom of the page, you'll " +
                "find a chart that displays how many coins you've earned each day, as well as the total for each week, month, " +
                "and year. It's easy to understand and visually appealing!"));
        ArrayList<AppDocsComments> docs_comment_2_3 = new ArrayList<>();
        docs_comment_2_3.add(new AppDocsComments("Payments", "Ready to pay? Simply click the first box " +
                "and you'll be taken straight to the payments activity!"));
        docs_comment_2_3.add(new AppDocsComments("Wallet", "Want to access your wallet? Just click on " +
                "the second box and you'll be directed straight to your wallet activity!"));
        docs_comment_2_3.add(new AppDocsComments("Reward", "Looking for your rewards? Simply scroll to " +
                "the right and click on the Reward box to access your 'My Reward' activity!"));
        ArrayList<ArrayList<AppDocsComments>> app_info_2 = new ArrayList<>();
        app_info_2.add(0, docs_comment_2_1);
        app_info_2.add(1, docs_comment_2_2);
        app_info_2.add(2, docs_comment_2_3);

        AppDocs app_doc_2 = new AppDocs(title_2, app_pictures_2, helper_txt_2, app_info_2);

        docsList.add(app_doc_2);


//        account

        String title_3 = "Inbox";

        ArrayList<Integer> app_pictures_3 = new ArrayList<>();
        app_pictures_3.add(R.drawable.firebase_user_inbox_1);
        app_pictures_3.add(R.drawable.firebase_user_inbox_message_2);

        String helper_txt_3 = "The inbox activity has two pages. The first page shows whether your transaction was successful " +
                "or failed, and if it failed, you can see why and make any necessary changes before trying again. " +
                "If the transaction failed, the money will be returned to your account. The second page shows the progress of " +
                "the transaction.";

        ArrayList<AppDocsComments> docs_comment_3_1 = new ArrayList<>();
        docs_comment_3_1.add(new AppDocsComments("Notification Button", "Stay up-to-date with your " +
                "notifications! Just click on the notification button to view all of your notifications in one place."));
        docs_comment_3_1.add(new AppDocsComments("Transaction Button", "Track your transactions with ease!" +
                " Click on the transaction button to view all of your previous transactions and track their progress."));
        docs_comment_3_1.add(new AppDocsComments("Search Bar", "Find what you're looking for quickly and" +
                " easily! Use the search bar to search for transactions or messages by date or any keyword."));
        ArrayList<AppDocsComments> docs_comment_3_2 = new ArrayList<>();
        docs_comment_3_2.add(new AppDocsComments("User Picture", "Your profile picture at a glance! Look " +
                "for the circle icon to see your image."));
        docs_comment_3_2.add(new AppDocsComments("User Name", "Identify yourself! Your username is " +
                "located to the right of your profile picture."));
        docs_comment_3_2.add(new AppDocsComments("Payment Status", "Check your payment status at a " +
                "glance! Look just below your username to see whether your payment was successful or failed."));
        docs_comment_3_2.add(new AppDocsComments("Time", "Stay on top of your notifications! Check " +
                "the time stamp in the upper right corner to see how long ago your notifications arrived."));
        docs_comment_3_2.add(new AppDocsComments("Massage", "Get the details you need! Check the " +
                "bottom of the Payment Status to see a message explaining whether the payment was successful or failed, " +
                "and which account the money was transferred to (if applicable)."));
        ArrayList<ArrayList<AppDocsComments>> app_info_3 = new ArrayList<>();
        app_info_3.add(0, docs_comment_3_1);
        app_info_3.add(1, docs_comment_3_2);

        AppDocs app_doc_3 = new AppDocs(title_3, app_pictures_3, helper_txt_3, app_info_3);

        docsList.add(app_doc_3);


//        Wallet

        String title_4 = "Wallet";

        ArrayList<Integer> app_pictures_4 = new ArrayList<>();
        app_pictures_4.add(R.drawable.firebase_user_wallet_1);
        app_pictures_4.add(R.drawable.firebase_user_wallet_bars_2);
        app_pictures_4.add(R.drawable.firebase_user_wallet_transactions_3);

        String helper_txt_4 = "In the wallet activity, you can view your total balance and daily earnings for the past 7 days, " +
                "and you can withdraw your earnings.";

        ArrayList<AppDocsComments> docs_comment_4_1 = new ArrayList<>();
        docs_comment_4_1.add(new AppDocsComments("User Picture", "Find your account settings easily! " +
                "Look for your user picture in the top right corner."));
        docs_comment_4_1.add(new AppDocsComments("Total Money", "Keep track of your available funds! " +
                "Check just below the 'Balance' text to see your total withdrawable amount."));
        docs_comment_4_1.add(new AppDocsComments("Exchange Button", "Ready to redeem your coins for cash? " +
                "Click on the 'Exchange' button to be taken to the Redeem Activity, where you can exchange your coins for money."));
        docs_comment_4_1.add(new AppDocsComments("Withdraw Button", "Ready to withdraw your money? Click on " +
                "the 'Withdraw' button to get started. Before you do, make sure your details are correct. If not, be sure to " +
                "update them first."));
        ArrayList<AppDocsComments> docs_comment_4_2 = new ArrayList<>();
        docs_comment_4_2.add(new AppDocsComments("Daily Coins Chart", "Track your daily earnings with " +
                "ease! This chart displays your daily coin earnings, and allows you to compare them across different days, " +
                "including today, yesterday, and the day before yesterday. Note that your account must be at least 3 to 4 days " +
                "old for this feature to display your earnings data. If your account is newer, this section will not appear " +
                "until you've accumulated enough data.."));
        ArrayList<AppDocsComments> docs_comment_4_3 = new ArrayList<>();
        docs_comment_4_3.add(new AppDocsComments("Name Of Earner", "Who's earning what? Check out the first" +
                " column to see the name of each earner."));
        docs_comment_4_3.add(new AppDocsComments("Date & Time of Withdrawal", "Stay up-to-date on withdrawals!" +
                " Just below the earner's name, you'll find the date and time that they requested their withdrawal."));
        docs_comment_4_3.add(new AppDocsComments("Payment Progress", "Keep track of your payment " +
                "progress! Just below the date and time of withdrawal request, you'll find a progress indicator. If the " +
                "payment failed, it will be displayed in red; if successful, it will be green."));
        docs_comment_4_3.add(new AppDocsComments("Payment Amount", "Know your withdrawal amount! The " +
                "last column displays how much money the earner requested to withdraw. If the payment failed, the amount will " +
                "be displayed with a minus (-) symbol, indicating that the money was returned to their PlayMe account. If the " +
                "payment was successful, it will be displayed with a plus (+) symbol, indicating that the money was transferred" +
                " to their bank account."));

        ArrayList<ArrayList<AppDocsComments>> app_info_4 = new ArrayList<>();
        app_info_4.add(0, docs_comment_4_1);
        app_info_4.add(1, docs_comment_4_2);
        app_info_4.add(1, docs_comment_4_3);

        AppDocs app_doc_4 = new AppDocs(title_4, app_pictures_4, helper_txt_4, app_info_4);

        docsList.add(app_doc_4);


//        Redeem

        String title_5 = "Redeem";

        ArrayList<Integer> app_pictures_5 = new ArrayList<>();
        app_pictures_5.add(R.drawable.firebase_user_redeem_1);

        String helper_txt_5 = "The redeem activity allows you to convert your coins into withdrawn money.";

        ArrayList<AppDocsComments> docs_comment_5_1 = new ArrayList<>();
        docs_comment_5_1.add(new AppDocsComments("Redeemable Coin's", "Convert your coins to PlayMe " +
                "money! At the top, you'll see how many coins you have that are redeemable, and you can easily exchange them" +
                " for cash."));
        docs_comment_5_1.add(new AppDocsComments("Result of Redeemable Coins in Money", "See your redeemable" +
                " coin balance! Just below the down arrow, you'll find the amount of money you can withdraw from your " +
                "redeemable coins."));
        docs_comment_5_1.add(new AppDocsComments("Exchange Button", "Ready to exchange your coins? " +
                "Look for the orange button! Click it to convert your coins into money. Don't worry, if you don't have " +
                "enough coins, the button won't be clickable."));

        ArrayList<ArrayList<AppDocsComments>> app_info_5 = new ArrayList<>();
        app_info_5.add(0, docs_comment_5_1);

        AppDocs app_doc_5 = new AppDocs(title_5, app_pictures_5, helper_txt_5, app_info_5);

        docsList.add(app_doc_5);


//        returning list do all operations before this
        return docsList;
    }

    private void unLoadDialog() {
        PlayMeUseAccountDocumentationBottomFragment.this.dismiss();
        Toast.makeText(requireActivity(), "Some Fault Occurs", Toast.LENGTH_SHORT).show();
    }


//    async task
public static class LoadInBackground extends AsyncTask<Void, Void, ArrayList<AppDocs>> {

        PlayMeUseAccountDocumentationBottomFragment ref;

        public LoadInBackground(PlayMeUseAccountDocumentationBottomFragment ref) {
            this.ref = ref;
        }

        @Override
        protected ArrayList<AppDocs> doInBackground(Void... voids) {
            return ref.loadAllAppDocs();
        }

        @Override
        protected void onPostExecute(ArrayList<AppDocs> result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.size() > 0) {
                    ref.setRecycler(result);
                } else {
                    ref.unLoadDialog();
                }
            } else {
                ref.unLoadDialog();
            }

        }
    }
}