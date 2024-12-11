package com.example.a1.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1.R;

import java.util.List;

import models.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.tvTitle.setText(expense.getTitle());
        holder.tvAmount.setText(String.format("%.0f đ", expense.getAmount()));
        holder.tvDate.setText(expense.getDate());
    }

    @Override
    public int getItemCount() {
        Log.d("ExpenseAdapter", "Số lượng bản ghi trong Adapter: " + expenses.size());
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount, tvDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExpenseName);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}
