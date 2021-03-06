package pl.librus.client.ui.attendances;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import pl.librus.client.R;
import pl.librus.client.domain.attendance.FullAttendance;
import pl.librus.client.domain.subject.Subject;
import pl.librus.client.util.LibrusUtils;

public class AttendanceItem extends AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem> {
    private final FullAttendance attendance;


    AttendanceItem(AttendanceHeaderItem header, FullAttendance attendance) {
        super(header);
        this.attendance = attendance;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_item;
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.shortName.setText(attendance.category().shortName());
        Context context = holder.itemView.getContext();
        String lessonNumber = context.getString(R.string.lesson) + " " + attendance.lessonNumber();
        holder.lesson.setText(lessonNumber);

        LibrusUtils.setTextViewValue(holder.subject, attendance.subject().transform(Subject::name));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendanceItem that = (AttendanceItem) o;

        return attendance.equals(that.attendance);

    }

    @Override
    public int hashCode() {
        return attendance.hashCode();
    }

    public FullAttendance getAttendance() {
        return attendance;
    }

    class ViewHolder extends FlexibleViewHolder {
        final TextView subject;
        final TextView lesson;
        final TextView shortName;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            subject = (TextView) view.findViewById(R.id.attendance_item_lesson);
            lesson = (TextView) view.findViewById(R.id.attendance_item_lesson_number);
            shortName = (TextView) view.findViewById(R.id.attendance_item_shortType);
        }
    }
}
