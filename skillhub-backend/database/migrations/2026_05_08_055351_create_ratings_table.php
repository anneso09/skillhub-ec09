<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('ratings', function (Blueprint $table) {
            $table->id();
            $table->foreignId('formation_id')->constrained()->onDelete('cascade');
            $table->unsignedBigInteger('apprenant_id');
            $table->tinyInteger('note')->unsigned(); // 1 à 5
            $table->text('commentaire')->nullable();
            $table->timestamps();

            // Un apprenant ne peut noter qu'une seule fois
            $table->unique(['formation_id', 'apprenant_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('ratings');
    }
};