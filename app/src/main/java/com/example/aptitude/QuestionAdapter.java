package com.example.aptitude;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final List<Question> questionList;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_detail, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.questionText.setText(question.getQuestionText());
        holder.correctAnswerText.setText(question.getCorrectAnswer());
        holder.answerDescriptionText.setText(question.getExplanation());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public void updateQuestions(ArrayList<Question> newQuestionList) {
        questionList.clear();
        questionList.addAll(newQuestionList);
        notifyDataSetChanged();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView questionText;
        TextView correctAnswerText;
        TextView answerDescriptionText;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            correctAnswerText = itemView.findViewById(R.id.correct_answer_text);
            answerDescriptionText = itemView.findViewById(R.id.answer_description_text);
        }


    }
}
