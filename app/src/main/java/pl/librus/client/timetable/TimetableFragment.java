package pl.librus.client.timetable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.items.IFlexible;
import pl.librus.client.R;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;
import pl.librus.client.sql.LibrusDbHelper;
import pl.librus.client.ui.MainFragment;

import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_CANCELED;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_DATE;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_ID;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_LESSON_NUMBER;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_ORG_SUBJECT_ID;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_ORG_TEACHER_ID;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_SUBJECT_ID;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_SUBJECT_NAME;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_SUBSTITUTION;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_TEACHER_FIRST_NAME;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_TEACHER_ID;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.COLUMN_NAME_TEACHER_LAST_NAME;
import static pl.librus.client.sql.LibrusDbContract.TimetableLessons.TABLE_NAME;

public class TimetableFragment extends Fragment implements MainFragment {
    final ProgressItem progressItem = new ProgressItem();
    public Runnable onSetupCompleted = new Runnable() {
        @Override
        public void run() {

        }
    };
    TimetableAdapter adapter;
    LinearLayoutManager layoutManager;
    LocalDate startDate = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
    int page = 0;

    public static TimetableFragment newInstance() {
        return new TimetableFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LibrusCacheLoader cacheLoader = new LibrusCacheLoader(getContext());
//        final LocalDate weekStart = LocalDate.now().withDayOfWeek(MONDAY);
//        cacheLoader.load(TimetableUtils.getFilenameForDate(weekStart)).done(new DoneCallback<LibrusCache>() {
//            @Override
//            public void onDone(LibrusCache result) {
//                SchoolWeek w = ((TimetableCache) result).getSchoolWeek();
//                addSchoolWeek(w);
//            }
//        }).fail(new FailCallback<String>() {
//            @Override
//            public void onFail(String result) {
//                APIClient client = new APIClient(getContext());
//                client.getSchoolWeek(weekStart).done(new DoneCallback<SchoolWeek>() {
//                    @Override
//                    public void onDone(SchoolWeek result) {
//                        addSchoolWeek(result);
//                    }
//                });
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //boolean useTabs = prefs.getBoolean("useTabs", false);

        //if (!useTabs) {

        //scroll to default position after layout is completed
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void onUpdateComplete() {
//                layoutManager.scrollToPositionWithOffset(adapter.getGlobalPositionOf(new LessonHeaderItem(LocalDate.now())), 0);
//            }
//        }, 50);

        /*} else {
            TabLayout tabs = (TabLayout) inflater.inflate(R.layout.tabs, null);
            ((MainActivity) getActivity()).addToolbarView(tabs);

            root = inflater.inflate(R.layout.fragment_timetable_tabs, container, false);

            List<SchoolDay> schoolDays = new ArrayList<>();
            for (SchoolWeek w : data.getSchoolWeeks()) schoolDays.addAll(w.getSchoolDays());
            Collections.sort(schoolDays);
            ViewPager viewPager = (ViewPager) root.findViewById(R.id.fragment_timetable_viewpager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager(), schoolDays);
            viewPager.setAdapter(adapter);

            tabs.setupWithViewPager(viewPager);

            viewPager.setCurrentItem(schoolDays.indexOf(new SchoolDay(LocalDate.now())));
        }
        */
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_timetable_recycler);

        recyclerView.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimetableAdapter(null);
        adapter.setDisplayHeadersAtStartUp(true);
        page = 0;
        adapter.setEndlessProgressItem(progressItem);

        LibrusDbHelper dbHelper = new LibrusDbHelper(getContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        adapter.onLoadMoreListener = new TimetableAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                List<IFlexible> newElements = new ArrayList<>();
                progressItem.setStatus(ProgressItem.LOADING);
                adapter.notifyItemChanged(adapter.getGlobalPositionOf(progressItem));
                final LocalDate weekStart = startDate.plusWeeks(page);

                for (LocalDate date = weekStart; date.isBefore(weekStart.plusWeeks(1)); date = date.plusDays(1)) {
                    final long dateMillis = date.toDateTimeAtStartOfDay().getMillis();
                    Cursor cursor = db.query(
                            TABLE_NAME,
                            null,
                            COLUMN_NAME_DATE + " = " + dateMillis,
                            null,
                            null,
                            null,
                            COLUMN_NAME_LESSON_NUMBER
                    );
                    LessonHeaderItem header = new LessonHeaderItem(date);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            int lessonNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_LESSON_NUMBER));
                            boolean substitution = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_SUBSTITUTION)) > 0;
                            boolean canceled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_CANCELED)) > 0;
                            Subject subject = new Subject(
                                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_SUBJECT_ID)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_SUBJECT_NAME)));
                            Teacher teacher = new Teacher(
                                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TEACHER_ID)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TEACHER_FIRST_NAME)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TEACHER_LAST_NAME)));
                            String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ID));
                            Lesson lesson;
                            if (canceled) {
                                lesson = new Lesson(id, lessonNumber, date, LocalTime.now(), LocalTime.now(), subject, teacher, true);
                            } else if (substitution) {
                                lesson = new Lesson(id, lessonNumber, date, LocalTime.now(), LocalTime.now(), subject, teacher,
                                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ORG_SUBJECT_ID)),
                                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ORG_TEACHER_ID)));
                            } else {
                                lesson = new Lesson(id, lessonNumber, date, LocalTime.now(), LocalTime.now(), subject, teacher);
                            }
                            LessonItem lessonItem = new LessonItem(header, lesson, getContext());
                            newElements.add(lessonItem);
                        }
                    } else {
                        newElements.add(new EmptyLessonItem(header, date));
                    }
                    cursor.close();
                }
                progressItem.setStatus(ProgressItem.IDLE);
                adapter.onLoadMoreComplete(newElements);
                if (page == 0) onSetupCompleted.run();
                page++;
            }
        }

        ;
        recyclerView.setAdapter(adapter);
        adapter.onLoadMoreListener.onLoadMore();
    }

    @Override
    public void refresh(LibrusData cache) {
    }
}
