package com.jmedeisis.example.draglinearlayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class NoteActivity extends ActionBarActivity {

    DragLinearLayout noteContainer;
    int firstNoteIndex;
    int noteCount;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteContainer = (DragLinearLayout) findViewById(R.id.noteContainer);
        String[] defaultNotes = getResources().getStringArray(R.array.notes);
        firstNoteIndex = noteContainer.getChildCount();
        for(int i = 0; i < defaultNotes.length; i++){
            addExistingNote(i, defaultNotes[i]);
        }
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        noteContainer.setContainerScrollView(scrollView);
        noteContainer.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
                final int firstIndex = getNoteIndex(firstView);
                final int secondIndex = getNoteIndex(secondView);
                setNoteIndex(firstView, secondIndex);
                setNoteIndex(secondView, firstIndex);

                Toast.makeText(NoteActivity.this, "Swapped note " + firstIndex + " with note " +
                                secondIndex, Toast.LENGTH_SHORT).show();
            }
        });

        noteCount = defaultNotes.length;
        addNewBlankNote(noteCount, false);
    }

    private void addExistingNote(int index, String text){
        final View note = View.inflate(this, R.layout.list_item_note, null);
        ((ImageView) note.findViewById(R.id.noteIcon)).setImageResource(R.drawable.ic_drag);

        setNoteIndex(note, index);

        final EditText noteText = (EditText) note.findViewById(R.id.noteText);
        noteText.setText(text);
        noteText.addTextChangedListener(new NoteTextWatcher(note));
        noteText.setOnFocusChangeListener(new NoteFocusChangeListener(note));
        noteText.setHint(R.string.note_complete_prompt);

        noteContainer.addDragView(note, note.findViewById(R.id.noteIconContainer));
    }

    private void addNewBlankNote(int index, boolean animateIn){
        final View note = View.inflate(this, R.layout.list_item_note, null);

        setNoteIndex(note, index);

        final EditText noteText = (EditText) note.findViewById(R.id.noteText);
        noteText.addTextChangedListener(new NoteTextWatcher(note));
        noteText.setOnFocusChangeListener(new NoteFocusChangeListener(note));

        noteContainer.addView(note);

        if(animateIn){
            final ViewTreeObserver observer = note.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);

                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(note, "alpha", 0, 1);
                    ObjectAnimator yAnimator = ObjectAnimator.ofFloat(note, "y",
                            note.getTop() + 20, note.getTop());
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(alphaAnimator, yAnimator);
                    set.setDuration(200);
                    set.start();

                    scrollView.smoothScrollBy(0, note.getHeight());
                    return true;
                }

            });
        }
    }

    /** Animates in a new blank note if text is entered. */
    private class NoteTextWatcher implements TextWatcher {
        private final View note;
        public NoteTextWatcher(final View note){
            this.note = note;
        }

        @Override
        public void afterTextChanged(Editable s) { }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(0 != start) return;
            if(count > 0 && before < 1){
                final int index = getNoteIndex(note);
                if(noteCount == index && getLastNoteIndex() == index){
                    addNewBlankNote(noteCount + 1, true); // not a very 'nice' way to pick next index..
                }
            } else if(0 == count && before > 0){
                // TODO remove note on additional backspace??
            }
        }
    }

    /** Toggles IME visibility and stores state on focus. */
    private class NoteFocusChangeListener implements View.OnFocusChangeListener {
        final View note;

        public NoteFocusChangeListener(View note){
            this.note = note;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            setImeVisibility(noteContainer, hasFocus);
            EditText editText = (EditText) v;
            final int index = getNoteIndex(note);

            if(hasFocus){
                editText.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            } else {
                if(editText.length() > 0){
                    ((ImageView)note.findViewById(R.id.noteIcon)).setImageResource(R.drawable.ic_drag);
                    noteContainer.setViewDraggable(note, note.findViewById(R.id.noteIconContainer));

                    if(index < noteCount){
                        // note at index set to new value
                    } else {
                        // new note added at index
                        noteCount++;
                    }

                    editText.setHint(R.string.note_complete_prompt);
                } else {
                    if(index < noteCount){
                        // existing note set blank
                    } else if(index < getLastNoteIndex()){
                        // too many trailing blank notes, remove last one
                        final View noteToDelete = noteContainer.getChildAt(firstNoteIndex + index + 1);
                        noteToDelete.findViewById(R.id.noteText).setEnabled(false); // disable further editing

                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(noteToDelete, "alpha", 1, 0);
                        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(noteToDelete, "x",
                                noteToDelete.getLeft(), noteToDelete.getLeft() + 30);
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(alphaAnimator, xAnimator);
                        set.setDuration(200);
                        set.addListener(new AnimatorListenerAdapter(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                noteContainer.removeView(noteToDelete);
                            }
                        });
                        set.start();
                    }
                }
                editText.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        }
    }

    /** Stores the index as the View's tag. */
    private void setNoteIndex(View note, int index){
        note.setTag(index);
    }
    /** @return the index stored as the View's tag, or -1 if a tag is not present. */
    private int getNoteIndex(View note){
        final Object tag = note.getTag();
        if(null == tag) return -1;

        return (Integer) tag;
    }

    private int getLastNoteIndex(){
        return noteContainer.getChildCount() - 1 - firstNoteIndex;
    }

    /*
     * IME fiddling
     * ---------------------------------------------------------------------------------------------
     */
    private Runnable toggleImeRunnable;
    /**
     * Opens or closes the IME.
     * If called in quick succession (i.e. before the message queue of the view is processed),
     * only the latest call will get executed.
     */
    private void setImeVisibility(final View view, final boolean visible) {
        if (visible) {
            view.removeCallbacks(toggleImeRunnable);
            toggleImeRunnable = new Runnable() {
                public void run() {
                    InputMethodManager imm = (InputMethodManager)
                            NoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (null != imm) {
                        imm.showSoftInput(view, 0);
                    }
                }
            };
            view.post(toggleImeRunnable);
        } else {
            view.removeCallbacks(toggleImeRunnable);
            toggleImeRunnable = new Runnable() {
                public void run() {
                    InputMethodManager imm = (InputMethodManager)
                            NoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (null != imm) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            };
            view.post(toggleImeRunnable);
        }
    }
}
