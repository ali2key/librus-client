package pl.librus.client.presentation;

import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.announcement.Announcement;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.announcements.AnnouncementItem;
import pl.librus.client.ui.announcements.AnnouncementsFragment;
import pl.librus.client.ui.announcements.AnnouncementsView;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class AnnouncementsPresenter extends MainFragmentPresenter<AnnouncementsView> {

    public static final int TITLE = R.string.announcements_view_title;
    private final UpdateHelper updateHelper;
    private final LibrusData data;

    @Inject
    public AnnouncementsPresenter(UpdateHelper updateHelper, LibrusData data, MainActivityOps mainActivity) {
        super(mainActivity);
        this.updateHelper = updateHelper;
        this.data = data;
    }

    @Override
    protected void onViewAttached() {
        refreshView();
    }

    public void reload() {
        updateHelper.reloadMany(
                Announcement.class,
                Teacher.class)
                .isEmpty()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(empty -> {
                    if (empty) {
                        view.setRefreshing(false);
                    } else {
                        refreshView();
                    }
                });
    }

    private void refreshView() {
        data.findFullAnnouncements()
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(l -> mainActivity.setBackArrow(false))
                .subscribe(view::display);
    }

    @Override
    public Fragment getFragment() {
        return new AnnouncementsFragment();
    }

    @Override
    public int getTitle() {
        return TITLE;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_announcement_black_48dp;
    }

    public void displayDetails(AnnouncementItem announcementItem) {
        mainActivity.setBackArrow(true);
        view.displayDetails(announcementItem);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}