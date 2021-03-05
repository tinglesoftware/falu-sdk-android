package tingle.software.falu.model

import com.google.gson.annotations.SerializedName
import java.io.File

/**
 * [The evaluation request object](https://falu.io)
 */
data class EvaluationRequest(
    /**
     * Represents the currency e.g kes
     */
    var currency: String = "kes",

    /**
     * Represents the scope within which an evaluation is generated.
     * This can also be considered the purpose of the evaluation.
     */
    var scope: EvaluationScope,

    /**
     * Represents the kind of provider used for a statement in an evaluation.
     *
     */
    var provider: StatementProvider = StatementProvider.MPESA,

    /**
     * The full name of the person or business that owns the statement.
     *
     */
    var name: String,

    /**
     * The Phone number for attached to the statement.
     * Only required for statements generated against a phone number e.g. mpesa
     *
     */
    var phone: String?,

    /**
     * Password to open the uploaded file. Only required for password protected files.
     * Certain providers only provide password protected files.
     * In such cases the password should always be provided.
     */
    var password: String?,

    /**
     * Statement file uploaded with the http request
     */
    var file: File,

    /**
     * A brief description of the request you are making
     */
    var description: String?,


    /**
     * Metadata
     */
    var metadata: Any? = null,

    /**
     * Tags
     */
    var tags: Array<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EvaluationRequest

        if (tags != null) {
            if (other.tags == null) return false
            if (!tags.contentEquals(other.tags)) return false
        } else if (other.tags != null) return false

        return true
    }

    override fun hashCode(): Int {
        return tags?.contentHashCode() ?: 0
    }
}


enum class EvaluationScope {
    @SerializedName("personal")
    PERSONAL,

    @SerializedName("business")
    BUSINESS;
}