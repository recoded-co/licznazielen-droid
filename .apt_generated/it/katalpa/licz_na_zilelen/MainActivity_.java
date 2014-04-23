//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package it.katalpa.licz_na_zilelen;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.inject.Injector;
import com.googlecode.androidannotations.api.BackgroundExecutor;
import com.googlecode.androidannotations.api.SdkVersionHelper;
import it.katalpa.licz_na_zilelen.R.id;
import it.katalpa.licz_na_zilelen.R.layout;
import it.katalpa.licz_na_zilelen.model.PleaceObject;
import roboguice.activity.event.OnActivityResultEvent;
import roboguice.activity.event.OnConfigurationChangedEvent;
import roboguice.activity.event.OnContentChangedEvent;
import roboguice.activity.event.OnContentViewAvailableEvent;
import roboguice.activity.event.OnCreateEvent;
import roboguice.activity.event.OnDestroyEvent;
import roboguice.activity.event.OnNewIntentEvent;
import roboguice.activity.event.OnPauseEvent;
import roboguice.activity.event.OnRestartEvent;
import roboguice.activity.event.OnResumeEvent;
import roboguice.activity.event.OnStartEvent;
import roboguice.activity.event.OnStopEvent;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectorProvider;

public final class MainActivity_
    extends MainActivity
    implements InjectorProvider
{

    private ContextScope scope_;
    private EventManager eventManager_;
    private Handler handler_ = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
    }

    private void init_(Bundle savedInstanceState) {
        Injector injector_ = getInjector();
        scope_ = injector_.getInstance(ContextScope.class);
        scope_.enter(this);
        injector_.injectMembers(this);
        eventManager_ = injector_.getInstance(EventManager.class);
        eventManager_.fire(new OnCreateEvent(savedInstanceState));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void afterSetContentView_() {
        scope_.injectViews();
        eventManager_.fire(new OnContentViewAvailableEvent());
        nearButton = ((Button) findViewById(id.nearButton));
        addButton = ((Button) findViewById(id.addButton));
        buttonSearch = ((ImageButton) findViewById(id.buttonSearch));
        headerText = ((TextView) findViewById(id.headerText));
        {
            View view = findViewById(id.buttonMenu);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        MainActivity_.this.showMenu();
                    }

                }
                );
            }
        }
        {
            View view = findViewById(id.buttonSearch);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        MainActivity_.this.showSearchDialog();
                    }

                }
                );
            }
        }
        {
            View view = findViewById(id.addButton);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        MainActivity_.this.addObject();
                    }

                }
                );
            }
        }
        {
            View view = findViewById(id.nearButton);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        MainActivity_.this.searchNear();
                    }

                }
                );
            }
        }
        initApp();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        afterSetContentView_();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        afterSetContentView_();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        afterSetContentView_();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((SdkVersionHelper.getSdkInt()< 5)&&(keyCode == KeyEvent.KEYCODE_BACK))&&(event.getRepeatCount() == 0)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static MainActivity_.IntentBuilder_ intent(Context context) {
        return new MainActivity_.IntentBuilder_(context);
    }

    @Override
    public void onRestart() {
        scope_.enter(this);
        super.onRestart();
        eventManager_.fire(new OnRestartEvent());
    }

    @Override
    public void onStart() {
        scope_.enter(this);
        super.onStart();
        eventManager_.fire(new OnStartEvent());
    }

    @Override
    public void onResume() {
        scope_.enter(this);
        super.onResume();
        eventManager_.fire(new OnResumeEvent());
    }

    @Override
    public void onPause() {
        super.onPause();
        eventManager_.fire(new OnPauseEvent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        scope_.enter(this);
        eventManager_.fire(new OnNewIntentEvent());
    }

    @Override
    public void onStop() {
        scope_.enter(this);
        try {
            eventManager_.fire(new OnStopEvent());
        } finally {
            scope_.exit(this);
            super.onStop();
        }
    }

    @Override
    public void onDestroy() {
        scope_.enter(this);
        try {
            eventManager_.fire(new OnDestroyEvent());
        } finally {
            eventManager_.clear(this);
            scope_.exit(this);
            scope_.dispose(this);
            super.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager_.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        eventManager_.fire(new OnContentChangedEvent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scope_.enter(this);
        try {
            eventManager_.fire(new OnActivityResultEvent(requestCode, resultCode, data));
        } finally {
            scope_.exit(this);
        }
    }

    @Override
    public Injector getInjector() {
        return ((InjectorProvider) getApplication()).getInjector();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(it.katalpa.licz_na_zilelen.R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void ClearMarkers() {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.ClearMarkers();
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void ShowNotFoundDialog() {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.ShowNotFoundDialog();
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void updateAdapter(final String sCut) {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.updateAdapter(sCut);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void refreshMap(final int iCount) {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.refreshMap(iCount);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void ShowFlashMessage(final int type, final String sMessage) {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.ShowFlashMessage(type, sMessage);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void addMarkerToMap(final PleaceObject po, final int i) {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.addMarkerToMap(po, i);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void ShowNearDialog(final List<PleaceObject> aObjectList) {
        handler_.post(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.ShowNearDialog(aObjectList);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void addObject(final PleaceObject obj) {
        BackgroundExecutor.execute(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.addObject(obj);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void getNearObjects(final LatLng ll, final LatLngBounds nowBounds) {
        BackgroundExecutor.execute(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.getNearObjects(ll, nowBounds);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    @Override
    public void searchForObjects(final String sSearch) {
        BackgroundExecutor.execute(new Runnable() {


            @Override
            public void run() {
                try {
                    MainActivity_.super.searchForObjects(sSearch);
                } catch (RuntimeException e) {
                    Log.e("MainActivity_", "A runtime exception was thrown while executing code in a runnable", e);
                }
            }

        }
        );
    }

    public static class IntentBuilder_ {

        private Context context_;
        private final Intent intent_;

        public IntentBuilder_(Context context) {
            context_ = context;
            intent_ = new Intent(context, MainActivity_.class);
        }

        public Intent get() {
            return intent_;
        }

        public MainActivity_.IntentBuilder_ flags(int flags) {
            intent_.setFlags(flags);
            return this;
        }

        public void start() {
            context_.startActivity(intent_);
        }

        public void startForResult(int requestCode) {
            if (context_ instanceof Activity) {
                ((Activity) context_).startActivityForResult(intent_, requestCode);
            } else {
                context_.startActivity(intent_);
            }
        }

    }

}
