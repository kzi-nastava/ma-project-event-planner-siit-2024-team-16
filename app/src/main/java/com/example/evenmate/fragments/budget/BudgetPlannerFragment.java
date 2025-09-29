package com.example.evenmate.fragments.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evenmate.R;
import com.example.evenmate.adapters.BudgetItemAdapter;
import com.example.evenmate.models.budget.BudgetItem;
import com.example.evenmate.models.budget.BudgetItemCreate;

import java.util.ArrayList;
import java.util.List;

public class BudgetPlannerFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";
    private BudgetPlannerViewModel viewModel;
    private BudgetItemAdapter adapter;
    private Long eventId;

    public static BudgetPlannerFragment newInstance(Long eventId) {
        BudgetPlannerFragment fragment = new BudgetPlannerFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget_planner, container, false);
        eventId = getArguments() != null ? getArguments().getLong(ARG_EVENT_ID, -1) : -1;
        viewModel = new ViewModelProvider(this).get(BudgetPlannerViewModel.class);
        if (eventId != -1) {
            viewModel.setEventId(eventId);
            viewModel.fetchBudgetItems();
        }

        RecyclerView rvBudgetItems = view.findViewById(R.id.rv_budget_items);
        TextView tvTotalBudget = view.findViewById(R.id.tv_total_budget);
        Button btnAddBudgetItem = view.findViewById(R.id.btn_add_budget_item);

        adapter = new BudgetItemAdapter();
        rvBudgetItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBudgetItems.setAdapter(adapter);

        viewModel.getBudgetItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setBudgetItems(items);
        });
        viewModel.getTotalBudget().observe(getViewLifecycleOwner(), total -> {
            tvTotalBudget.setText(String.valueOf(total));
        });

        adapter.setActionListener(new BudgetItemAdapter.OnBudgetItemActionListener() {
            @Override
            public void onDetails(BudgetItem item) {
                AssetListDialogFragment assetDialog = new AssetListDialogFragment(
                        item.getCategoryName(),
                        item.getId(),
                        (assetId, type) -> {
                            if (type.equalsIgnoreCase("PURCHASE")) {
                                Bundle args = new Bundle();
                                args.putLong("product_id", assetId);
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                                navController.navigate(R.id.action_budgetPlannerFragment_to_productDetailsFragment, args);
                            } else if (type.equalsIgnoreCase("RESERVATION")) {
                                Bundle args = new Bundle();
                                args.putLong("service_id", assetId);
                                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                                navController.navigate(R.id.action_budgetPlannerFragment_to_serviceDetailsFragment, args);
                            }
                        });
                assetDialog.show(getParentFragmentManager(), "AssetListDialog");
            }
            @Override
            public void onEdit(BudgetItem item) {
                EditBudgetItemDialogFragment editDialog = new EditBudgetItemDialogFragment(item, newAmount -> {
                    viewModel.editBudgetItem(item.getId(), newAmount);
                });
                editDialog.show(getParentFragmentManager(), "EditBudgetItemDialog");
            }
            @Override
            public void onDelete(BudgetItem item) {
                viewModel.deleteBudgetItem(item.getId());
            }
        });

        btnAddBudgetItem.setOnClickListener(v -> {
            AddBudgetItemDialogFragment addDialog = new AddBudgetItemDialogFragment(eventId, (categoryId, amount) -> {
                BudgetItemCreate budgetItem = new BudgetItemCreate(amount, eventId, categoryId);
                viewModel.addBudgetItem(budgetItem);
            });
            addDialog.show(getParentFragmentManager(), "AddBudgetItemDialog");
        });

        return view;
    }
}
