package avatar.hardware

import avatar.Avatar
import com.pi4j.context.Context
import runtime.setup.Configuration

class AvatarBuilder(private val pi4j: Context, private val configuration: Configuration) {

    fun build(): Avatar {
        val avatar = Avatar().build(pi4j, configuration)
        return avatar
    }
}