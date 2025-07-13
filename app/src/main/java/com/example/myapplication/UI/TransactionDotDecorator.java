package com.example.myapplication.UI;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class TransactionDotDecorator implements DayViewDecorator {

    private final CalendarDay day;
    private final List<Integer> colors;

    public TransactionDotDecorator(CalendarDay day, List<Integer> colors) {
        this.day = day;
        this.colors = colors;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        if (colors != null && !colors.isEmpty()) {
            view.addSpan(new MultiDotSpan(colors));
        }
    }

    private static class MultiDotSpan implements LineBackgroundSpan {

        private final List<Integer> colors;

        public MultiDotSpan(List<Integer> colors) {
            this.colors = colors;
        }

        @Override
        public void drawBackground(
                Canvas canvas, Paint paint,
                int left, int right, int top, int baseline, int bottom,
                CharSequence charSequence, int start, int end, int lineNum) {

            int radius = 6;
            int space = 8;
            int totalWidth = radius * 2 * colors.size() + space * (colors.size() - 1);
            int centerX = (left + right) / 2;
            int y = bottom + 8;

            int startX = centerX - totalWidth / 2;

            for (int i = 0; i < colors.size(); i++) {
                paint.setColor(colors.get(i));
                canvas.drawCircle(startX + i * (radius * 2 + space), y, radius, paint);
            }
        }
    }
}
