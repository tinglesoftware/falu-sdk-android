package io.falu.identity.capture.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.falu.identity.IdentityVerificationViewModel
import io.falu.identity.R
import io.falu.identity.ai.DocumentDetectionOutput
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.api.models.verification.Verification
import io.falu.identity.camera.CameraView
import io.falu.identity.capture.AbstractCaptureFragment.Companion.getIdentityDocumentName
import io.falu.identity.capture.scan.utils.DocumentScanDisposition
import io.falu.identity.capture.scan.utils.ScanResult
import io.falu.identity.databinding.FragmentScanCaptureBinding
import io.falu.identity.documents.DocumentSelectionFragment


internal class ScanCaptureFragment(identityViewModelFactory: ViewModelProvider.Factory) :
    Fragment() {

    private val identityViewModel: IdentityVerificationViewModel by activityViewModels { identityViewModelFactory }
    private val documentScanViewModel: DocumentScanViewModel by activityViewModels()

    private var _binding: FragmentScanCaptureBinding? = null
    private val binding get() = _binding!!

    private var scanType: DocumentScanDisposition.DocumentScanType? = null
    private lateinit var identityDocumentType: IdentityDocumentType

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        identityDocumentType =
            requireArguments().getSerializable(DocumentSelectionFragment.KEY_IDENTITY_DOCUMENT_TYPE) as IdentityDocumentType

        scanType =
            requireArguments().getSerializable(KEY_DOCUMENT_SCAN_TYPE) as? DocumentScanDisposition.DocumentScanType

        documentScanViewModel.resetScanDispositions()

        resetUI()

        binding.tvScanMessage.text = getString(
            R.string.scan_capture_text_scan_message,
            identityDocumentType.getIdentityDocumentName(requireContext())
        )

        val inputStream = resources.openRawResource(R.raw.model)
        val file = identityViewModel.getModel(inputStream, "model.tflite")

        documentScanViewModel.initialize(file, 0.5f)

        startScan(scanType!!)

        binding.viewCamera.lifecycleOwner = viewLifecycleOwner
        binding.viewCamera.lensFacing = CameraSelector.LENS_FACING_BACK

        binding.viewCamera.cameraViewType =
            if (identityDocumentType != IdentityDocumentType.PASSPORT) CameraView.CameraViewType.ID else CameraView.CameraViewType.PASSPORT


        identityViewModel.observeForVerificationResults(
            viewLifecycleOwner,
            onSuccess = { onVerificationPage(it) },
            onError = {}
        )

        documentScanViewModel.documentScanDisposition.observe(viewLifecycleOwner) {
            updateUI(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startScan(scanType: DocumentScanDisposition.DocumentScanType) {
        documentScanViewModel.scanner?.scan(binding.viewCamera.analyzers, scanType)
    }

    private fun resetUI() {
        when {
            scanType!!.isFront -> {
                binding.tvScanDocumentSide.text = getString(
                    R.string.scan_capture_text_document_side_front,
                    identityDocumentType.getIdentityDocumentName(requireContext())
                )
            }
            scanType!!.isBack -> {
                binding.tvScanDocumentSide.text = getString(
                    R.string.scan_capture_text_document_side_back,
                    identityDocumentType.getIdentityDocumentName(requireContext())
                )
            }
        }
    }

    private fun updateUI(result: ScanResult) {
        when (result.disposition) {
            is DocumentScanDisposition.Start -> {
                resetUI()
            }
            is DocumentScanDisposition.Detected -> {
                binding.tvScanMessage.text = getString(R.string.scan_capture_text_document_detected)
            }
            is DocumentScanDisposition.Desired -> {
                binding.tvScanMessage.text =
                    getString(R.string.scan_capture_text_document_scan_completed)
            }
            is DocumentScanDisposition.Undesired -> {}
            is DocumentScanDisposition.Completed -> {}
            is DocumentScanDisposition.Timeout, null -> { //noOP
            }
        }
    }

    private fun onVerificationPage(verification: Verification) {
        documentScanViewModel.documentScanCompleteDisposition.observe(viewLifecycleOwner) {
            if (it.disposition is DocumentScanDisposition.Completed) {
                // stop the analyzer
                binding.viewCamera.stopAnalyzer()
                binding.viewCamera.analyzers.clear()

                when {
                    scanType!!.isFront -> {
                        setFragmentResult(
                            REQUEST_KEY_DOCUMENT_SCAN,
                            bundleOf(KEY_SCAN_TYPE_FRONT to it.output)
                        )
                        findNavController().navigateUp()
                    }

                    scanType!!.isBack -> {
                        setFragmentResult(
                            REQUEST_KEY_DOCUMENT_SCAN,
                            bundleOf(KEY_SCAN_TYPE_BACK to it.output)
                        )
                        findNavController().navigateUp()
                    }
                }
            } else {
                // something else.
            }
        }
    }

    internal companion object {
        internal const val KEY_DOCUMENT_SCAN_TYPE = ":scan-type"
        internal const val KEY_SCAN_TYPE_FRONT = ":front"
        internal const val KEY_SCAN_TYPE_BACK = ":back"
        internal const val REQUEST_KEY_DOCUMENT_SCAN = ":scan"

        internal fun IdentityDocumentType.getScanType(): Pair<DocumentScanDisposition.DocumentScanType, DocumentScanDisposition.DocumentScanType?> {
            return when (this) {
                IdentityDocumentType.IDENTITY_CARD -> {
                    Pair(
                        DocumentScanDisposition.DocumentScanType.KENYA_DL_FRONT,
                        DocumentScanDisposition.DocumentScanType.KENYA_ID_BACK
                    )
                }
                IdentityDocumentType.PASSPORT -> {
                    Pair(
                        DocumentScanDisposition.DocumentScanType.PASSPORT,
                        null
                    )
                }
                IdentityDocumentType.DRIVING_LICENSE -> {
                    Pair(
                        DocumentScanDisposition.DocumentScanType.KENYA_DL_FRONT,
                        DocumentScanDisposition.DocumentScanType.KENYA_DL_BACK
                    )
                }
            }
        }
    }
}