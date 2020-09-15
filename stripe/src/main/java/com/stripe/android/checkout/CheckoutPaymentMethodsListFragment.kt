package com.stripe.android.checkout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stripe.android.R
import com.stripe.android.checkout.CheckoutViewModel.Factory
import com.stripe.android.checkout.CheckoutViewModel.SelectedPaymentMethod
import com.stripe.android.checkout.CheckoutViewModel.TransitionTarget
import com.stripe.android.databinding.FragmentCheckoutPaymentMethodsListBinding

internal class CheckoutPaymentMethodsListFragment : Fragment(R.layout.fragment_checkout_payment_methods_list) {
    private val viewModel by activityViewModels<CheckoutViewModel> {
        Factory(requireActivity().application)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity == null) {
            return
        }

        val binding = FragmentCheckoutPaymentMethodsListBinding.bind(view)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val selectedPaymentMethodId =
            (viewModel.selectedPaymentMethod.value as? SelectedPaymentMethod.Saved)?.paymentMethod?.id

        viewModel.paymentMethods.observe(viewLifecycleOwner) { paymentMethods ->
            binding.recycler.adapter = CheckoutPaymentMethodsAdapter(
                paymentMethods,
                selectedPaymentMethodId = selectedPaymentMethodId,
                addCardClickListener = { viewModel.transitionTo(TransitionTarget.AddCard) },
                paymentMethodSelectedListener = { pm ->
                    viewModel.setSelectedPaymentMethod(
                        pm?.let {
                            SelectedPaymentMethod.Saved(it)
                        }
                    )
                }
            )
        }

        // Only fetch the payment methods list if we haven't already
        if (viewModel.paymentMethods.value == null) {
            viewModel.updatePaymentMethods(requireActivity().intent)
        }
    }
}
