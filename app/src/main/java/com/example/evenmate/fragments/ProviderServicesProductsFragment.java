package com.example.evenmate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.evenmate.R;
import com.example.evenmate.activities.CreateServiceActivity;
import com.example.evenmate.adapters.ServiceAdapter;
import com.example.evenmate.models.service.Service;
import java.util.ArrayList;
import java.util.List;

public class ProviderServicesProductsFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_services_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.servicesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ServiceAdapter adapter = new ServiceAdapter(createMockServices(), service -> {
            Bundle bundle = new Bundle();
            bundle.putLong("SERVICE_ID", service.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_providerServicesProducts_to_serviceDetailsFragment, bundle);
        });
        recyclerView.setAdapter(adapter);


        // Setup FAB
        view.findViewById(R.id.create_service_floating_button).setOnClickListener(v ->
//                navController.navigate(R.id.action_providerServicesProducts_to_createService)
                startActivity(new Intent(requireContext(), CreateServiceActivity.class)));
    }

    private List<Service> createMockServices() {
        List<Service> services = new ArrayList<>();

        //        services.add(new Service(
//                "1",
//                "Professional Photography",
//                "Photography",
//                "Service",
//                199.99,
//                true,
//                true,
//                R.drawable.img_service
//        ));
//
//        services.add(new Service(
//                "2",
//                "Wedding Venue",
//                "Venue",
//                "Service",
//                1499.99,
//                true,
//                true,
//                R.drawable.img_service
//        ));
//
//        services.add(new Service(
//                "3",
//                "Catering Package",
//                "Catering",
//                "Product",
//                299.99,
//                false,
//                true,
//                R.drawable.img_service
//        ));
//
//        services.add(new Service(
//                "4",
//                "DJ Services",
//                "Music",
//                "Service",
//                399.99,
//                true,
//                true,
//                R.drawable.img_service
//        ));
//
//        services.add(new Service(
//                "5",
//                "Decoration Set",
//                "Decoration",
//                "Product",
//                199.99,
//                true,
//                true,
//                R.drawable.img_service
//        ))
        
        return services;
    }
}