package io.falu.identity.screens.capture

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.falu.identity.R
import io.falu.identity.api.models.IdentityDocumentType
import io.falu.identity.ui.theme.IdentityTheme

@Composable
internal fun DocumentCaptureView(
    title: String,
    documentType: IdentityDocumentType,
    isFrontLoading: Boolean = false,
    isBackLoading: Boolean = false,
    isFrontUploaded: Boolean,
    isBackUploaded: Boolean,
    onFront: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.content_padding_normal))
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.element_spacing_normal)),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.content_padding_normal))
        ) {
            DocumentCard(
                title = stringResource(
                    R.string.upload_document_capture_document_front,
                    stringResource(documentType.titleRes)
                ),
                buttonText = stringResource(id = R.string.button_select_front),
                onSelectClicked = { onFront() },
                isUploaded = isFrontUploaded,
                loading = isFrontLoading
            )

            if (documentType != IdentityDocumentType.PASSPORT) {
                DocumentCard(
                    title = stringResource(
                        R.string.upload_document_capture_document_back,
                        stringResource(documentType.titleRes)
                    ),
                    buttonText = stringResource(id = R.string.button_select_back),
                    onSelectClicked = { onBack() },
                    isUploaded = isBackUploaded,
                    loading = isBackLoading
                )
            }
        }
    }
}

@Composable
private fun DocumentCard(
    title: String,
    buttonText: String,
    onSelectClicked: () -> Unit,
    loading: Boolean,
    isUploaded: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.content_padding_normal)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.element_spacing_normal))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.element_spacing_normal)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = dimensionResource(R.dimen.element_spacing_normal))
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isUploaded && !loading) {
                    TextButton(onClick = onSelectClicked) {
                        Text(text = buttonText)
                    }
                }

                if (!isUploaded && loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(dimensionResource(R.dimen.content_padding_normal)),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = dimensionResource(R.dimen.element_spacing_normal_quarter),
                        trackColor = Color.LightGray
                    )
                }

                if (isUploaded) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_circle),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.content_padding_normal))
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun DocumentCapturePreview() {
    IdentityTheme {
        DocumentCaptureView(
            title = stringResource(
                id = R.string.upload_document_capture_title,
                stringResource(IdentityDocumentType.IDENTITY_CARD.titleRes)
            ),
            documentType = IdentityDocumentType.IDENTITY_CARD,
            isFrontUploaded = false,
            isBackUploaded = false,
            onFront = {},
            onBack = {}
        )
    }
}