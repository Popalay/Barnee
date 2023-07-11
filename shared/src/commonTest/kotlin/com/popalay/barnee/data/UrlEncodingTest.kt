package com.popalay.barnee.data

import com.eygraber.uri.UriCodec
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlEncodingTest {

    @Test
    fun testDecodedUrlMatchesOriginal() {
        val url =
            "https://oaidalleapiprodscus.blob.core.windows.net/private/org-8Am4P1OK8R9wdEaiOoPfiqsr/user-SK45uIVFqdA0dfOYVHTH0sjs/img-1U5l285YxmX23qziu8FxYiPB.png?st=2023-07-11T09%3A57%3A51Z&se=2023-07-11T11%3A57%3A51Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-07-11T02%3A42%3A41Z&ske=2023-07-12T02%3A42%3A41Z&sks=b&skv=2021-08-06&sig=sERZZx5b5olXN06kl1y7GhOYmD3VIZI2/7LiytjE1LI%3D"

        val encodedUrl = UriCodec.encode(url)
        val decodedUrl = UriCodec.decode(encodedUrl)

        assertEquals(url, decodedUrl)
    }
}
