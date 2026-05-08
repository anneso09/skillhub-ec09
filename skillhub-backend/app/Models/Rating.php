<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Rating extends Model
{
    protected $fillable = [
        'formation_id',
        'apprenant_id',
        'note',
        'commentaire',
    ];

    public function formation()
    {
        return $this->belongsTo(Formation::class);
    }
}