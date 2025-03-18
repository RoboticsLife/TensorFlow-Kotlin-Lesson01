package brain

import avatar.Avatar

class BrainBuilder(private val avatar: Avatar) {

    fun build() = Brain().build(avatar)

}