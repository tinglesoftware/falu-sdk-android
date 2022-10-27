package io.falu.identity.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import io.falu.identity.IdentityVerificationViewModel
import io.falu.identity.R
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.api.models.country.SupportedCountry
import io.falu.identity.api.models.verification.Verification
import io.falu.identity.databinding.FragmentDocumentSelectionBinding
import io.falu.identity.utils.navigateToApiResponseProblemFragment
import io.falu.identity.utils.updateVerification
import software.tingle.api.patch.JsonPatchDocument

class DocumentSelectionFragment : Fragment() {

    private val viewModel: IdentityVerificationViewModel by activityViewModels()

    private var _binding: FragmentDocumentSelectionBinding? = null
    private val binding get() = _binding!!

    private var identityDocumentType: IdentityDocumentType? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDocumentSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observerForSupportedCountriesResults(
            viewLifecycleOwner,
            onSuccess = { onSupportedCountriesListed(it.toList()) },
            onError = {
                navigateToApiResponseProblemFragment(it)
            }
        )

        binding.buttonContinue.text = getString(R.string.button_continue)
        binding.buttonContinue.isEnabled = false
        binding.buttonContinue.setOnClickListener {
            binding.buttonContinue.showProgress()
            val country = binding.buttonContinue.tag as SupportedCountry
            val document = JsonPatchDocument()
                .replace("country", country.country.code)

            updateVerification(
                viewModel,
                document,
                source = R.id.action_fragment_welcome_to_fragment_document_selection,
                onSuccess = {
                    val bundle = bundleOf(KEY_IDENTITY_DOCUMENT_TYPE to identityDocumentType)
                    findNavController().navigate(
                        R.id.action_fragment_document_selection_to_fragment_document_capture_methods,
                        bundle
                    )
                })
        }

        binding.groupDocumentTypes.setOnCheckedStateChangeListener { group, checkIds ->
            binding.buttonContinue.isEnabled = checkIds.isNotEmpty()

            when (group.checkedChipId) {
                R.id.chip_passport -> identityDocumentType = IdentityDocumentType.PASSPORT
                R.id.chip_identity_card -> identityDocumentType = IdentityDocumentType.IDENTITY_CARD
                R.id.chip_driving_license -> identityDocumentType =
                    IdentityDocumentType.DRIVING_LICENSE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     *
     */
    private fun onSupportedCountriesListed(countries: List<SupportedCountry>) {
        val countriesAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            countries.map { it.country.name })

        binding.inputAssetIssuingCountry.setAdapter(countriesAdapter)
        binding.inputAssetIssuingCountry.setText(countriesAdapter.getItem(0), false)

        val country = getSupportedCountry(countries)
        getVerificationResults(country)

        binding.inputAssetIssuingCountry.setOnItemClickListener { _, _, _, _ ->
            getVerificationResults(country)
        }
    }

    /**
     *
     */
    private fun getSupportedCountry(countries: List<SupportedCountry>): SupportedCountry {
        val country = binding.inputAssetIssuingCountry.text.toString()
        return countries.first { it.country.name == country }
    }

    /**
     *
     */
    private fun getVerificationResults(country: SupportedCountry) {
        binding.buttonContinue.tag = country
        viewModel.observeForVerificationResults(
            viewLifecycleOwner,
            onSuccess = { acceptedDocumentOptions(it, country) },
            onError = {}
        )
    }

    /**
     *
     */
    private fun acceptedDocumentOptions(verification: Verification, country: SupportedCountry) {
        val acceptedDocuments =
            verification.options.document.allowed.toSet().intersect(country.documents.toSet())

        binding.chipIdentityCard.isEnabled =
            acceptedDocuments.contains(IdentityDocumentType.IDENTITY_CARD)
        binding.chipPassport.isEnabled =
            acceptedDocuments.contains(IdentityDocumentType.PASSPORT)
        binding.chipDrivingLicense.isEnabled =
            acceptedDocuments.contains(IdentityDocumentType.DRIVING_LICENSE)
    }

    internal companion object {
        const val KEY_IDENTITY_DOCUMENT_TYPE = ":document-type"
    }
}